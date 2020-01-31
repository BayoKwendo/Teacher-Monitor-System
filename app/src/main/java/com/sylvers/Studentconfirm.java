package com.sylvers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Studentconfirm extends AppCompatActivity {

    ArrayList<String> courses = new ArrayList<>();
    ArrayList<String> coursesName = new ArrayList<>();
    String teacherNumber;
    private ListView lv;

    // Set up the menu.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Activity Title.
        setTitle("Teachers");

        String uid = getIntent().getStringExtra("uid");

        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // ListView Reference.
        lv = findViewById(R.id.listViewCourses);

        // Add Listener to fill the data.
        // Resolve query for courses names.
        Query coursesQuery = database.getReference("teachers-courses");

        // Event listenere to update.
        coursesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    // Grab the key.
                    String key = courseSnapshot.getKey();

//                            Toast.makeText(Courses.this, "
                    // If key is required.
//                            if (courses.contains(key)){
                    String courseName = courseSnapshot.child("Fullame").getValue().toString();
//
//                            Toast.makeText(Courses.this, ""+ courseName, Toast.LENGTH_SHORT).show();

                    teacherNumber = courseSnapshot.child("teacherNo").getValue().toString();

                    coursesName.add(courseName);

                }

//                        holder.mView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent topicsIntent = new Intent(QuestionsActivity.this, PrivateActivity.class);
//                                topicsIntent.putExtra("forumKey", getIntent().getStringExtra("forumKey"));
//                                topicsIntent.putExtra("topicKey", getRef(position).getKey());
//                                topicsIntent.putExtra("topicName", model.getTopicName());
//                                startActivity(topicsIntent);
//
//                            }
//                        });

                // Fill the list.
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        Studentconfirm.this,
                        android.R.layout.simple_list_item_1,
                        coursesName);

                // Set the adapter.
                lv.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView tagText = (TextView) view.findViewById(R.id.txt_text);
//                String tag = tagText.getText().toString();
//                Toast.makeText(getApplicationContext(),
//                        "Element Name " + tag + " Clicked", Toast.LENGTH_SHORT).show();


                String selected = (String) parent.getItemAtPosition(position);


                AlertDialog.Builder builder = new AlertDialog.Builder(Studentconfirm.this);
                builder.setMessage("Are sure this teacher attended the lesson??");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {


                        Query coursesQuery = FirebaseDatabase.getInstance()
                                .getReference("attendance").orderByChild("Fullname").
                                        equalTo(selected).limitToLast(1000000000);

                        // Event listenere to update.
                        coursesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {

                                      courseSnapshot.getRef().child("attend").setValue(true);
                                    Toast.makeText(Studentconfirm.this, "DONE", Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());

                            }
                        });


                    }
                });
                builder.create().show();



                return false;
            }
        });

    }





        @Override
        public void onBackPressed () {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Studentconfirm.super.onBackPressed();

                            Intent login = new Intent(Studentconfirm.this, LoginActivity.class);
                            startActivity(login);

                            FirebaseAuth.getInstance().signOut();

                            // Finish currentActivity.
                            finish();
                        }
                    }).create().show();
        }

    }
