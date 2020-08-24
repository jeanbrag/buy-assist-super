package com.iotsuper.buyassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
    Cadastro Activity - Nesta tela faremos o cadastro de um usuário com o Firebase
    Possui 3 Inputs: Email, Senha e Confirmação da Senha

    Observe que a Classe está implementando a Interface View.OnClickListener,
     isso serve para facilitar os cliques dos botões, ficando todos em apenas um método.
 */

public class CadastroActivity extends AppCompatActivity implements View.OnClickListener {
    //Instanciação dos Botões e Inputs
    private EditText editText_Email, editText_Senha, editText_RepetirSenha;
    private Button button_CadastrarUsuario, button_Cancelar;

    private FirebaseAuth auth; //Instanciação da variável de Autenticação
    private FirebaseFirestore db; //Instanciação do banco de dados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Inicializando os Inputs
        editText_Email = (EditText) findViewById(R.id.editText_EmailCadastro);
        editText_Senha = (EditText) findViewById(R.id.editText_SenhaCadastro);
        editText_RepetirSenha = (EditText) findViewById(R.id.editText_SenhaRepetirCadastro);

        //Inicializando os Botões
        button_CadastrarUsuario = (Button) findViewById(R.id.button_Recuperar);
        button_Cancelar = (Button) findViewById(R.id.button_Cancelar);

        //Implentação dos cliques - O uso do "this" só é possível devido a Interface View.OnClickListener
        button_Cancelar.setOnClickListener(this);
        button_CadastrarUsuario.setOnClickListener(this);

        auth = FirebaseAuth.getInstance(); //Inicializando a variável de autenticação
        db = FirebaseFirestore.getInstance(); //Inicializando o banco de dados
    }

    /*
            onClick(): Método da Interface View.OnClickListener, responsável pelo gerenciamento dos cliques com o uso do Switch Case
         */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_Recuperar:
                cadastrar();
                break;

            case R.id.button_Cancelar:
                cancelar();
                break;
        }
    }
    /*
            cadastrar(): Método que verifica se os inputs de email e senha são válidos e chama o método criarUsuário();
         */
    private void cadastrar() {

        //Recebendo as entradas do usuário com o getText(). **O uso do trim() é para retirar os espaços extras**
        String email = editText_Email.getText().toString().trim();
        String senha = editText_Senha.getText().toString();
        String senhaRepetida = editText_RepetirSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty() || senhaRepetida.isEmpty())
            Toast.makeText(getBaseContext(), "Erro - Campos Vazios", Toast.LENGTH_LONG).show();
        else {
            if (!senha.contentEquals(senhaRepetida))
                Toast.makeText(getBaseContext(), "Erro - Senhas diferentes", Toast.LENGTH_LONG).show();
            else {
                //Verifica se o usuário está conectado, e então chama o criarUsuario();
                if(Utility.statusInternet_MoWi( getBaseContext() ))
                    criarUsuario(email,senha);
                else Toast.makeText(getBaseContext(), "Erro - Sem conexão com a Internet", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void cancelar(){
        Toast.makeText(this, "Cancelar Clicado", Toast.LENGTH_LONG).show();
    }

    /*
        criarUsuario(email,senha): Este método é responsável por criar um usuário por email e senha
     */
    private void criarUsuario(String email, String senha){
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getBaseContext(),"Cadastro Efetuado Com Sucesso", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getBaseContext(),LoginActivity.class));
                    finish();
                }
                else {
                    String resposta = task.getException().toString();
                    Utility.opcoesErro(getBaseContext(), resposta);
                }
            }
        });

        criarDocumentoUsuario(email);

        Toast.makeText(this, "Cadastrar Clicado", Toast.LENGTH_LONG).show();
    }

    /*
        criarDocumentoUsuario
     */
    private void criarDocumentoUsuario(String email) {
        Data data = new Data();
        db.collection("users").document(email)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "bd criado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}