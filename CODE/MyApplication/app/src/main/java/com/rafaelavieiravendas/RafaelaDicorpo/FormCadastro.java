package com.rafaelavieiravendas.RafaelaDicorpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {

    private ImageView imageView;
    private EditText editext_nome, editext_email, editext_telefone, editext_senha, editext_senha2;
    private Button button_Cadastro;
    String[] messages = {"Preencha todos os campos", "Cadastro realizado com sucesso", "Campos de senha não condizem"};
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        iniciarComponentes();


        button_Cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editext_nome.getText().toString();
                String email = editext_email.getText().toString();
                String telefone = editext_telefone.getText().toString();
                String senha = editext_senha.getText().toString();
                String senha2 = editext_senha2.getText().toString();

                if(nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || senha.isEmpty() || senha2.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, messages[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else if(!senha.equals(senha2)){
                    Snackbar snackbar = Snackbar.make(v, messages[2], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    CadastrarUsuario(v);
                }
            }
        });
    }

    private void CadastrarUsuario(View v){

        String email = editext_email.getText().toString();
        String senha = editext_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    SalvarDadosUsuario();

                    Snackbar snackbar = Snackbar.make(v, messages[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    String erro;
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        erro = "Senha muito curta";
                    }catch(FirebaseAuthUserCollisionException e){
                        erro = "Email já cadastrado";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        erro = "Email inválido";
                    }catch (Exception e){
                        erro = "Erro ao cadastrar usuário, verifique os campos";
                    }
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    private void SalvarDadosUsuario(){

        String nome = editext_nome.getText().toString();
        String telefone = editext_telefone.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("telefone", telefone);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID).collection("Identidade").document("Usuario");
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar dados no Banco");
            }
        })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("db_error", "Erro ao salvar dados no Banco" + e.toString());
                }
            });
    }

    private void iniciarComponentes(){
        //imageView = findViewById(R.id.back_cadastro);
        editext_nome = findViewById(R.id.editext_nome);
        editext_email = findViewById(R.id.editext_email);
        editext_telefone = findViewById(R.id.editext_telefone);
        editext_senha = findViewById(R.id.editext_senha);
        editext_senha2 = findViewById(R.id.editext_senha2);
        button_Cadastro = findViewById(R.id.button_Cadastro);
    }
}