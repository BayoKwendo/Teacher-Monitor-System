package com.sylvers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.R.id.message;

public class LessonAttended extends AppCompatActivity {

    ArrayList<String> courses= new ArrayList<>();
    ArrayList<String> coursesName = new ArrayList<>();
    private ListView lv;
    String ids;

//     Set up the menu.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Activity Title.
        setTitle("Lessons Taught");


        Intent intent = getIntent();
        // Extract the name.
         ids = intent.getStringExtra("courseName");




        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // ListView Reference.
        lv  = (ListView) findViewById(R.id.listViewCourses);

        // Add Listener to fill the data.
                      // Resolve query for courses names.
                Query coursesQuery = database.getReference("attendance").orderByChild("Fullname").equalTo(ids).limitToLast(1000000000);

                // Event listenere to update.
                coursesQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                            // Grab the key.


                            adds();


                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());

                    }
                });




        // Listener for item click.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // Create new intent.
//                Intent intent = new Intent(LessonAttended.this, CourseDashboard.class);
                // Grab the item clicked.
//                String item = (String) parent.getItemAtPosition(position);
//                // Attach the string to new intent.
//                intent.putExtra("courseName", item);
//                // Start the new activity.
//                startActivity(intent);

                Toast.makeText(LessonAttended.this,  ids + " taught this lesson" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    void adds(){

        Query coursesQuery = FirebaseDatabase.getInstance().getReference("attendance").orderByChild("attend").equalTo(true).limitToLast(1000000000);

        // Event listenere to update.
        coursesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                    // Grab the key.
                    String key = courseSnapshot.getKey();



//                            Toast.makeText(Courses.this, ""+key, Toast.LENGTH_SHORT).show();


                    // If key is required.
//                            if (courses.contains(key)){
                    String courseName = courseSnapshot.child("courseName").getValue().toString();

                    String time = courseSnapshot.child("currentDate").getValue().toString();
//
//                                        Toast.makeText(LessonAttended.this, ""+ courseName, Toast.LENGTH_SHORT).show();



//                             coursesName.add(time);

                    coursesName.add(courseName);


                }

                // Fill the list.
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        LessonAttended.this,
                        android.R.layout.simple_list_item_1,
                        coursesName );

                // Set the adapter.
                lv.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Lofcate MenuItem with ShareActionProvider

        // Return true to display menu
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as Forums specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {

            // Create new intent.
            Intent intent = new Intent(LessonAttended.this, StudentReportView.class);
            // Grab the item clicked.

            intent.putExtra("courseName", ids);

            // Start the new activity.
            startActivity(intent);
        }
//        if (id == R.id.nav) {

        return super.onOptionsItemSelected(item);
    }

}
