package com.sylvers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText mUsernameInput, mSubject1, mSubject2, mSubject3, mEmailInput, mPasswordInput, mConfPasswordInput, mPhoneInput;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        mUsernameInput = findViewById(R.id.input_username);
        mSubject1 = findViewById(R.id.subject1);
        mSubject2 = findViewById(R.id.subject2);
        mEmailInput = findViewById(R.id.input_sign_up_email_address);
        mPasswordInput = findViewById(R.id.input_sign_up_password);
        mPhoneInput = findViewById(R.id.input_phone_number);
        mConfPasswordInput = findViewById(R.id.input_confirm_password);
        mCreateBtn = findViewById(R.id.btn_sign_up);
        TextView back_to_login = findViewById(R.id.login);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing up...");
        mProgress.setCancelable(true);

        mAuth = FirebaseAuth.getInstance();
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                    // the user will receive another verification email.
                    sendVerificationEmail();
                }
                // ...
            }
        };
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameInput.getText().toString().trim();
                String subject1 = mSubject1.getText().toString().trim();
                String subject2 = mSubject2.getText().toString().trim();
                String email = mEmailInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();
                String confPass = mConfPasswordInput.getText().toString().trim();
                String phone = mPhoneInput.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(username)) {
                    mUsernameInput.setError("Name required");
                    return;
                }
                if (username.length() <= 6) {
                    mUsernameInput.setError("Enter valid fullname ");
                    return;
                }

                if (TextUtils.isEmpty(subject1)) {
                    mUsernameInput.setError("Subject required");
                    return;
                }
                if (TextUtils.isEmpty(subject2)) {
                    mUsernameInput.setError("Subject required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmailInput.setError("This field is required");
                    return;
                }
                if (!(email.matches(emailPattern))) {

                    mEmailInput.setError("Email not Valid");


                }


                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("This field is required");
                    return;
                }

                if (TextUtils.isEmpty(confPass)) {
                    mConfPasswordInput.setError("This field is required");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhoneInput.setError("This field is required");
                    return;
                }

                if (phone.length()<10 ) {
                    mPhoneInput.setError("Invalid Phone Number");
                    return;
                }

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(confPass)) {
                    if (!confPass.equals(password)) {
                        Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    } else {
                        createUser(username, subject1, subject2, email, phone, password);

                    }
                }

            }
        });
    }


    private void createUser(final String username, final String subject1, final String subject2, final String email, final String phone, String password) {

        mProgress.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

//
//                                // Show Logged in Msg.
//                                Log.d("Logged In", "signInWithEmail:success");
//                                Toast.makeText(SignupActivity.this, "Authenticated.",
//                                        Toast.LENGTH_SHORT).show();

                    sendVerificationEmail();
                       postSubject(  username,   subject1,   subject2,   phone, email);


//                    // Get current user.
//
//                                // Forward to next activity.
//                                Intent intent = new Intent(SignupActivity.this, TeacherDashboard.class);
//                                // Attach the user data.
//                                intent.putExtra("uid", user.getUid());
//                                startActivity(intent);
//                                finish();



                                mProgress.dismiss();



                } else {
                    mProgress.dismiss();
                    Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }




    private void postSubject( String username,  String subject1,  String subject2,  String phone, String email) {
        mProgress.show();

        Map userMap = new HashMap();
        userMap.put("phoneNumber", phone);
        userMap.put("Fullame", username);

        userMap.put("teacherNo", mAuth.getCurrentUser().getUid());

        userMap.put("courses", subject1);

        userMap.put("email", email);
        userMap.put("courseNo", subject2);


        FirebaseDatabase.getInstance().getReference().child("teachers-courses").push()
                .setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    // Show Logged in Msg.
//                    Log.d("Logged In", "signInWithEmail:success");
//                    Toast.makeText(SignupActivity.this, "Authenticated.",
//                            Toast.LENGTH_SHORT).show();

                    // Get current user.
//                    FirebaseUser user = mAuth.getCurrentUser();

//                    // Forward to next activity.
//                    Intent intent = new Intent(SignupActivity.this, TeacherDashboard.class);
//                    // Attach the user data.
//                    intent.putExtra("uid", user.getUid());
//                    startActivity(intent);

                    Toast.makeText(SignupActivity.this, "Check your email for verification", Toast.LENGTH_SHORT).show();
                    finish();


                } else {

                    mProgress.dismiss();
                    Toast.makeText(SignupActivity.this, "Error! Please check your connection.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
