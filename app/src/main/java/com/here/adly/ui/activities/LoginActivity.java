package com.here.adly.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.here.adly.R;
import com.here.adly.models.User;
import com.here.adly.preferences.SessionManager;


public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Button btnRegister, btnLogin;
    private SessionManager sessionManager;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSession()) {
            startMainActivity();
        } else {

            setContentView(R.layout.activity_login);
            etEmail = findViewById(R.id.editTextLoginEmailadres);
            etPassword = findViewById(R.id.editTextLoginPassword);
            btnRegister = findViewById(R.id.tvLoginRegister);
            mAuth = FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog(this);
            btnLogin = findViewById(R.id.buttonLogin);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginUser();
                }
            });

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startRegisterActivity();
                }
            });
        }

    }

    private boolean checkSession() {

        sessionManager = new SessionManager(LoginActivity.this);
        String sessionId = sessionManager.getSession();

        if (sessionId.equals("none")) {
            return false;
        }
        return true;
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void LoginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();


        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            return;
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter your password");
            return;
        }

        progressDialog.setMessage("Please wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(mAuth.getCurrentUser().getUid().toString(), mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail());
                    sessionManager.saveSession(user);
                    startMainActivity();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

}