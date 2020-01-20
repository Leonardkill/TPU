package com.todasporuma;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.FirebaseDatabase;
import com.todasporuma.helper.Base64Helper;
import com.todasporuma.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword,edtEndereco,edtCelular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.editNome);
        edtEmail = findViewById(R.id.editEmail);
        edtPassword = findViewById(R.id.editPassword);
        edtEndereco = findViewById(R.id.edit_endereco);
        edtCelular = findViewById(R.id.edit_celular);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView btnLogin = findViewById(R.id.btnLogin);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(edtCelular,smf);
        edtCelular.addTextChangedListener(mtw);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String endereco = edtEndereco.getText().toString();
                String celular = edtCelular.getText().toString();

                if (!name.isEmpty()) {
                    if (!email.isEmpty()) {
                        if (!password.isEmpty()) {
                            User user = new User(name,email,endereco,celular);
                            user.setName(name);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setEndereco(endereco);
                            user.setCelular(celular);

                            registerUser(user);

                        } else {
                            edtPassword.setError("A senha deve ser preechida");
                            edtPassword.requestFocus();
                        }

                    } else {
                        edtEmail.setError("E-mail em Branco");
                        edtEmail.requestFocus();
                    }


                } else {
                    edtName.setError("Nome em Branco");
                    edtName.requestFocus();
                }


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    public void registerUser(final User user) {

        FirebaseAuth auth = FirebaseConfiguration.getFirebaseAutenticacao();
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String name_user = edtName.getText().toString();
                    String email_user = edtEmail.getText().toString();
                    String endereco_user = edtEndereco.getText().toString();
                    String celular_user = edtCelular.getText().toString();

                    User user = new User(name_user,email_user,endereco_user,celular_user);

                    FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);

                    //String userId = Base64Helper.encodeBase64(user.getEmail());
                    //user.setUserId(userId);
                    //user.save();

                    Toast.makeText(RegisterActivity.this, "Sucesso ao Cadastar", Toast.LENGTH_SHORT).show();
                    startActivity(LoginActivity.createIntent(RegisterActivity.this));
                } else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Digite um email válido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Email já cadastrado";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
