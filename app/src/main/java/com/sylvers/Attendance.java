package com.sylvers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class Attendance extends AppCompatActivity {

    private ArrayList<Student> students = new ArrayList<>();
    private ListView lv;
    private StudentsAttendanceList list;
    private String courseKey;
    private String courseName;
    private FirebaseAuth mAuth;

    TextView text;



//    // Set up the menu.
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    // Listener for clicks.
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // If the back button is pressed.
//        if (item.getItemId() == android.R.id.home) {
//            super.onBackPressed();
//            return true;
//        }
//        // Create new intent.
//        Intent login = new Intent(Attendance.this, MainActivity.class);
//        startActivity(login);
//        // Finish currentActivity.
//        finish();
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_attendance);
        // Set the activity title.
        setTitle("Attendance");

        mAuth = FirebaseAuth.getInstance();


        // Grab the course name from intent.
        Intent intent = getIntent();
        // Extract the name.
        courseName = intent.getStringExtra("courseName");
        final ArrayList<String> studentKeys = new ArrayList<>();

        text = findViewById(R.id.name);


        // Grab data from database.
        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // ListView Reference.
        lv = (ListView) findViewById(R.id.lvStudents);

        // Add Listener to fill the data.

        DatabaseReference dbRef = database.getReference("courses");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    final Boolean[] found = {false};
                    for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                        // Grab the course name.
                        String tempCourseName = courseSnapshot.child("name").getValue().toString();
                        // Grab the course key.
                        if (tempCourseName.equals(courseName)) {

                            // Resolve query for courses names.
                            DatabaseReference coursesQuery = database.getReference("teachers-courses");

                            // Event listenere to update.
                            coursesQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshots) {
//                                    for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
//                                        // Grab the key.
//                                        String key = courseSnapshot.getKey();
//
//                                        String courseName = courseSnapshot.child("name").getValue().toString();
////
//                                }
                                    for (DataSnapshot courseSnapshot : dataSnapshots.getChildren()) {
                                        String key = courseSnapshot.child("teacherNo").getValue().toString();
                                        studentKeys.add(key);

                                    }
                                    found[0] = true;

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("The read failed: " + databaseError.getCode());

                                }
                            });


                        }
                        // Break when key is found.
                        if (found[0]) break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        // Finally grab the students.
        // Build Query for the table.


        String uid = mAuth.getCurrentUser().getUid();
        final Query stdQuery = database.getReference("teachers-courses").orderByChild("teacherNo").equalTo(uid);

        stdQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    // Grab the key.
                    String stdKey = courseSnapshot.getKey();
                    // Match the key.
                    // Grab the student.
//                    Student std = courseSnapshot.getValue(Student.class);

                    String key = courseSnapshot.child("Fullame").getValue().toString();


                    text.setText(key);

                    Student std = courseSnapshot.getValue(Student.class);

                    students.add(std);



                }

                // Creating object of custom view item.
                list = new StudentsAttendanceList(Attendance.this, students);
                lv.setAdapter(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });

//
        // Add Listener to search the data.
        // Grab the edit text view.
        final EditText editTxt = (EditText)findViewById(R.id.etSearch);
        editTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Grab radio button to identify search type.
                RadioGroup rg = (RadioGroup)findViewById(R.id.rgSearch);
                // Grab the value of radio button.
                String value = ((RadioButton)findViewById(rg.getCheckedRadioButtonId()))
                        .getText().toString();
                // Get the Search Query.
                String text = editTxt.getText().toString().toLowerCase(Locale.getDefault());

                // Check the search type.
                if (value.equals("Name")) {
                    list.filterByName(text);
                } else {
                    list.filterByRollNo(text);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

        /**
         * Marks all the student present in current view.
         *
         * @param view Current View
         */
    public void markAllPresent(View view) {
        // Iterate over each view and mark the attendance.
        for (int i = 0; i < lv.getCount(); i++) {
            // Grab the child.
            View v = lv.getChildAt(i);
            // Grab radio group.
            RadioGroup rg = v.findViewById(R.id.rgAttendance);

            rg.clearCheck();
            // Grab the present button.
            RadioButton rb = rg.findViewById(R.id.rbPresent);
            // Set it true.
         }
    }

    /**
     * Marks all the student absent in current view.
     *
     * @param view Current View
     */
    public void markAllAbsent(View view) {
        // Iterate over each view and mark the attendance.
        for (int i = 0; i < lv.getCount(); i++) {
            // Grab the child.
            View v = lv.getChildAt(i);
            // Grab radio group.
            RadioGroup rg = v.findViewById(R.id.rgAttendance);
            // Grab the present button.
            RadioButton rb = rg.findViewById(R.id.rbAbsent);
            // Set it true.
            rb.setChecked(false);
        }
    }

    /**
     * Saves the current Attendance.
     *
     * @param view Current View.
     */
    public void saveAttendance(View view) {
        list.saveAttendance(courseName);

        Intent login = new Intent(Attendance.this, TeacherDashboard.class);
        startActivity(login);
        // Finish currentActivity.
        finish();

    }

}
