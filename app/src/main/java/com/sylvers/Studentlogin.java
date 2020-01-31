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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Studentlogin extends AppCompatActivity {

    private EditText mEmailInput, mPasswordInput, mEmailInput1, mPasswordInput1;
    private TextView mSignUpTv, mReset, Admin;
    private Button mLoginBtn, mLoginBtn1;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private ConstraintLayout layout1, layout2;

    private static final String TAG = Studentlogin.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student);
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {

            if (mAuth.getCurrentUser() != null) {
                Intent loginIntent = new Intent(Studentlogin.this, TeacherDashboard.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                finish();
            }

        };

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Authenticating...");
        mProgress.setCancelable(true);
        mEmailInput = findViewById(R.id.input_login_email);
        mPasswordInput = findViewById(R.id.input_login_password);
        mSignUpTv = findViewById(R.id.tv_sign_up);
        mReset = findViewById(R.id.tv_forgot_password);
        Admin = findViewById(R.id.admin);


        mLoginBtn = findViewById(R.id.btn_login);


        layout1 = findViewById(R.id.layout1);


        mLoginBtn = findViewById(R.id.btn_login);

        mLoginBtn = findViewById(R.id.btn_login);
        mSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Studentlogin.this, SignupActivity.class));
            }
        });
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Studentlogin.this, ResetPasswordActivity.class));
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(email)) {
                    mEmailInput.setError("Email Required");
                    return;
                }
                if (!(email.matches(emailPattern))){

                    mEmailInput.setError("Email not Valid");


                }


                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("Password Required");
                    return;
                }

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    login(email, password);
                }
            }
        });

        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Studentlogin.this, LoginActivity.class));

            }
        });

    }

    private void login(String email, String password) {

        mProgress.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    // Show Logged in Msg.
                    Log.d("Logged In", "signInWithEmail:success");


                    // Get current user.
                    String user = mAuth.getCurrentUser().getEmail();

                    assert user != null;
                    if (user.equals("studentauthenticating@gmail.com")){

                        Toast.makeText(Studentlogin.this, "Welcome!", Toast.LENGTH_SHORT).show();

                        // Forward to next activity.
                        Intent intent = new Intent(Studentlogin.this, Studentconfirm.class);
                        // Attach the user data.
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(Studentlogin.this, "Your Not the Student !!", Toast.LENGTH_SHORT).show();
                    }


                    mProgress.dismiss();


                } else {
                    mProgress.dismiss();

                    Log.w("NOT LOGGED IN", "signInWithEmail:failure", task.getException());

                    Toast.makeText(Studentlogin.this, "Failed to sign in. Please try again!!", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Studentlogin.this, MainActivity.class));

    }
}
