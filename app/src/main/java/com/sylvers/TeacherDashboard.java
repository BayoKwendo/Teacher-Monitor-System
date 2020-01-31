package com.sylvers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TeacherDashboard extends AppCompatActivity {

    TextView text1;
    private FirebaseAuth mAuth;
    TextView mErrorTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);
        // Set Title.
        setTitle("Dashboard");

        text1 = findViewById(R.id.textView5);


        mAuth = FirebaseAuth.getInstance();

        TextView mErrorTv = findViewById(R.id.tv_discussions_error);


        // Set onClick Listeners for the CardViews.
        // For Course Report


        ConstraintLayout cvCourseReport = (ConstraintLayout) findViewById(R.id.cvCoursesTeaching);
        cvCourseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent.
                Intent intent = new Intent(TeacherDashboard.this, Courses.class);
                // Grab the extra from previous intent.
                String uid = getIntent().getStringExtra("uid");
                // Attach the string to new intent.
                intent.putExtra("uid", uid);
                // Start the new activity.
                startActivity(intent);
            }
        }
        );


        String uid =  FirebaseAuth.getInstance().getUid();

        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query dbQuery = database.getReference("teachers-courses").orderByChild("teacherNo").equalTo(uid);

        // ListView Reference.

        // Add Listener to fill the data.
        dbQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                    String teachername = courseSnapshot.child("Fullame").getValue().toString();

                    text1.setText(teachername);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }




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
//        // Create new intent.
//        Intent login = new Intent(TeacherDashboard.this, LoginActivity.class);
//        startActivity(login);
//
//        mAuth.signOut();
//        // Finish currentActivity.
//        finish();
//        return super.onOptionsItemSelected(item);
//    }

    // Backpress control.
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        TeacherDashboard.super.onBackPressed();

                        mAuth.signOut();
                        startActivity(new Intent(TeacherDashboard.this, LoginActivity.class));

                        // Finish currentActivity.


                    }
                }).create().show();
    }
}
