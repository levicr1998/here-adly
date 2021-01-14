package com.here.adly.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;
import com.here.adly.models.User;
import com.here.adly.preferences.SessionManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    private DatabaseFB databaseFB;
    private TextInputLayout etFullName, etEmail, etPhone, etPassword, etRepeatPassword;
    private TextView tvLogin;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = mAuth.getInstance();
        this.databaseFB = new DatabaseFB();
        etEmail = findViewById(R.id.editTextRegisterEmailadres);
        etFullName = findViewById(R.id.editTextRegisterFullname);
        etPhone = findViewById(R.id.editTextRegisterPhone);
        etPassword = findViewById(R.id.editTextRegisterPassword);
        tvLogin = findViewById(R.id.tvRegisterLogin);
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

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    private void registerUser() {
        String email = etEmail.getEditText().getText().toString();
        String fullname = etFullName.getEditText().getText().toString();
        String phoneNumber = etPhone.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        String repeatPassword = etRepeatPassword.getEditText().getText().toString();

        if (isEmptyField(etEmail, "Enter your email", email) || isNotValidEmail(etEmail,email,"Invalid email")) {
            return;
        } else if (isEmptyField(etFullName, "Enter your full name", fullname)) {
            return;
        } else if (isEmptyField(etPhone, "Enter your phone number", phoneNumber)) {
            return;
        } else if (isEmptyField(etPassword, "Enter your password", password) || isFieldNotLongEnough(etPassword,"Password must be long as 6 letters",password)) {
            return;
        } else if (isEmptyField(etRepeatPassword, "Confirm your password", repeatPassword)) {
            return;
        } else if (checkFieldAreNotEqual(etRepeatPassword, password, repeatPassword, "The passwords are not equal")) {
            return;
        }


        progressDialog.setMessage("Please wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.createUserWithEmailAndPassword(email, password).

                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                                        databaseFB.mDatabase.child("Users").child(FBuser.getUid()).child("phoneNumber").setValue(phoneNumber);
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

    private Boolean isEmptyField(TextInputLayout field, String errorMessage, String fieldData) {
        if (TextUtils.isEmpty(fieldData)) {
            field.setError(errorMessage);
            return true;
        } else {
            field.setError(null);
            field.setErrorEnabled(false);
            return false;
        }

    }

    private Boolean isFieldNotLongEnough(TextInputLayout field, String errorMessage, String fieldData){
        if(fieldData.length() <= 6){
            field.setError(errorMessage);
            return true;
        } else {
            field.setError(null);
            field.setErrorEnabled(false);
            return false;
        }
    }

    private Boolean checkFieldAreNotEqual(TextInputLayout field, String firstfieldData, String secondFieldData, String errorMessage) {
        if (!firstfieldData.equals(secondFieldData)) {
            field.setError(errorMessage);
            return true;
        } else {
            field.setError(null);
            field.setErrorEnabled(false);
            return false;
        }
    }

    private Boolean isNotValidEmail(TextInputLayout field, CharSequence target, String errorMessage) {
        if (!Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            field.setError(errorMessage);
            return true;
        } else {
            field.setError(null);
            field.setErrorEnabled(false);
            return false;
        }
    }
}