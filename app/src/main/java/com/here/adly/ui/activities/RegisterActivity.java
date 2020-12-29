package com.here.adly.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.here.adly.R;
import com.here.adly.models.User;
import com.here.adly.preferences.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    private EditText etFullName, etEmail, etPassword, etRepeatPassword;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = mAuth.getInstance();
        etEmail = findViewById(R.id.editTextLoginEmailadres);
        etFullName = findViewById(R.id.editTextRegisterFullname);
        etPassword = findViewById(R.id.editTextRegisterPassword);
        etRepeatPassword = findViewById(R.id.editTextRegisterRepeatPassword);
        btnRegister = findViewById(R.id.buttonRegister);
        progressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(RegisterActivity.this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }


    private void registerUser() {
        String email = etEmail.getText().toString();
        String fullname = etFullName.getText().toString();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();


        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            return;
        } else if (TextUtils.isEmpty(fullname)) {
            etFullName.setError("Enter your fullname");
            return;
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter your password");
            return;
        } else if (TextUtils.isEmpty(repeatPassword)) {
            etPassword.setError("Confirm your password");
            return;
        } else if (TextUtils.isEmpty(repeatPassword)) {
            etRepeatPassword.setError("Confirm your password!");
            return;
        } else if (!password.equals(repeatPassword)) {
            etRepeatPassword.setError("The passwords are note equal");
        } else if (!isValidEmail(email)) {
            etEmail.setError("Invalid email");
            return;
        }

        progressDialog.setMessage("Please wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(mAuth.getInstance().getCurrentUser().getUid(), fullname, email);

                    FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Succesfully registered", Toast.LENGTH_LONG).show();
                                FirebaseUser FBuser = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(user.getFullName()).build();
                                FBuser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sessionManager.saveSession(user);
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }


    private Boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}