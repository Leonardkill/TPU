package com.todasporuma;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.todasporuma.helper.SharedPreferenceHelper;
import com.todasporuma.helper.ToastHelper;
import com.todasporuma.model.User;

import static com.todasporuma.common.Constants.EMAIL_KEY;
import static com.todasporuma.common.Constants.PASSWORD_KEY;

public class LoginActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkLogin();

        edtEmail = findViewById(R.id.editEmail);
        edtPassword = findViewById(R.id.editPassword);
        Button login = findViewById(R.id.btnLogin);
        TextView register = findViewById(R.id.btnRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity.createIntent(LoginActivity.this));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if (!email.isEmpty()) {
                    if (!password.isEmpty()) {
                        User user = new User();
                        user.setEmail(email);
                        user.setPassword(password);

                        validate(user);
                    } else {
                        edtEmail.setError("E-mail em Branco");
                        edtEmail.requestFocus();
                    }
                } else {
                    edtPassword.setError("Senha em Branco");
                    edtPassword.requestFocus();
                }
            }
        });
    }

    private void checkLogin() {

        String email = SharedPreferenceHelper.getSharedPreferenceString(this, EMAIL_KEY, null);
        String password = SharedPreferenceHelper.getSharedPreferenceString(this, PASSWORD_KEY, null);

        if (email != null && password != null) {
            openMapsActivity();
        }
    }

    private void validate(final User user) {
        FirebaseAuth auth = FirebaseConfiguration.getFirebaseAutenticacao();
        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferenceHelper.setSharedPreferenceString(LoginActivity.this, EMAIL_KEY, user.getEmail());
                    SharedPreferenceHelper.setSharedPreferenceString(LoginActivity.this, PASSWORD_KEY, user.getPassword());
                    openMapsActivity();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        ToastHelper.make(LoginActivity.this, "Usuario inválido");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        ToastHelper.make(LoginActivity.this, "Senha inválida");
                    } catch (Exception e) {
                        ToastHelper.make(LoginActivity.this, "Erro ao cadastrar usuário: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void openMapsActivity() {
        startActivity(MapsActivity.createIntent(LoginActivity.this));
        finish();
    }
}
