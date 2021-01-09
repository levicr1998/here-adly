package com.here.adly.ui.activities;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.here.adly.R;
import com.here.adly.models.User;
import com.here.adly.preferences.SessionManager;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextView tvRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Button btnLogin;
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

            tilEmail = findViewById(R.id.editTextLoginEmailadres);
            tilPassword = findViewById(R.id.editTextLoginPassword);
            tvRegister = findViewById(R.id.tvLoginRegister);
            mAuth = FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog(this);
            btnLogin = findViewById(R.id.buttonLogin);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginUser();
                }
            });

            tvRegister.setOnClickListener(new View.OnClickListener() {
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


    private void LoginUser() {
        String email = tilEmail.getEditText().getText().toString().trim();
        String password = tilPassword.getEditText().getText().toString();


        if (isEmptyField(tilEmail, "Enter your email", email) || isNotValidEmail(tilEmail,email,"Invalid email")) {
            return;
        } else if (isEmptyField(tilPassword, "Enter your password", password)) {
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
                    Toast.makeText(LoginActivity.this, "Wrong email and password combination", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

}