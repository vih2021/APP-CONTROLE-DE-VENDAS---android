package com.rafaelavieiravendas.RafaelaDicorpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FormLogin extends AppCompatActivity {

    private TextView text_cadastrar;
    private EditText  editext_email, editext_senha;
    private Button button_login;
    private ProgressBar progress_bar;
    String[] messages = {"Preencha todos os campos", "Email e/ou Senha incorretos"};

    private TextView edittext_senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        //getSupportActionBar().hide(); //esconder aquela barra roxa
        iniciarComponentes();

        text_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLogin.this, FormCadastro.class);
                startActivity(intent);
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editext_email.getText().toString();
                String senha = editext_senha.getText().toString();
                if(email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, messages[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    AutenticarUsuario(v);
                }
            }
        });
    }

    private void AutenticarUsuario(View v){
        String email = editext_email.getText().toString();
        String senha = editext_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progress_bar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelaPrincipal();
                        }
                    },1000);
                }else{
                    String erro;
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        erro = messages[1];
                    }
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioAtual != null){
            TelaPrincipal();
        }
    }

    private void TelaPrincipal(){
        Intent intent = new Intent(FormLogin.this, TelaPrincipal.class);
        startActivity(intent);
        finish();
    }

    private void iniciarComponentes(){
        text_cadastrar = findViewById(R.id.text_cadastrar);
        editext_email = findViewById(R.id.editext_email);
        editext_senha = findViewById(R.id.editext_senha);
        button_login = findViewById(R.id.button_login);
        progress_bar = findViewById(R.id.progress_bar);
    }
}