package com.here.adly.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
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
import com.here.adly.R;
import com.here.adly.models.User;
import com.here.adly.preferences.SessionManager;


public class LoginActivity extends AppCompatActivity {

    private EditText ETemail, ETpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Button buttonRegister, buttonLogin;
    private SessionManager sessionManager;

    @Override
    protected void onStart() {
        super.onStart();

        checkSession();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ETemail = findViewById(R.id.editTextLoginEmailadres);
        ETpassword = findViewById(R.id.editTextLoginPassword);
        buttonRegister = findViewById(R.id.tvLoginRegister);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterActivity();
            }
        });


    }

    private void checkSession() {

        sessionManager = new SessionManager(LoginActivity.this);
        String sessionId = sessionManager.getSession();
        System.out.println(sessionId);
        if (!sessionId.equals("none")) {
            startMainActivity();
        }

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
        String email = ETemail.getText().toString();
        String password = ETpassword.getText().toString();


        if (TextUtils.isEmpty(email)) {
            ETemail.setError("Enter your email");
            return;
        } else if (TextUtils.isEmpty(password)) {
            ETpassword.setError("Enter your password");
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