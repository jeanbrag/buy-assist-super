package com.iotsuper.buyassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
    First Activity - Nesta tela teremos 2 botões: Login e Cadastro

    Observe que a Classe está implementando a Interface View.OnClickListener,
     isso serve para facilitar os cliques dos botões, ficando todos em apenas um método.
 */

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button_Login, button_Cadastrar;  //Instanciando os Botões

    private FirebaseAuth auth;   //Variável de Autenticação com o Firebase

    private FirebaseUser user;   //Variável do Usuário autenticado

    private FirebaseAuth.AuthStateListener authStateListener;  // "Ouvinte": Este é responsável por "vigiar" o estado da autenticação


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //Inicializando os Botões
        button_Login = (Button) findViewById(R.id.button_Login);
        button_Cadastrar = (Button) findViewById(R.id.button_Cadastrar);

        //Implentação dos cliques - O uso do "this" só é possível devido a Interface View.OnClickListener
        button_Login.setOnClickListener(this);
        button_Cadastrar.setOnClickListener(this);

        auth = FirebaseAuth.getInstance(); //Inicializando a variável com a autenticação atual
        authState();
    }
    /*
            onClick(): Método da Interface View.OnClickListener, responsável pelo gerenciamento dos cliques com o uso do Switch Case
         */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            /*
                Aqui temos algo interessante: caso já exista um usuário autenticado, o botão de login chamará diretamente
                a tela principal do aplicativo, senão houver iremos para a tela de login.
            */
            case R.id.button_Login:
                user = auth.getCurrentUser();
                if(user == null)
                    startActivity(new Intent(this, LoginActivity.class));
                else
                    startActivity(new Intent(this, PrincipalActivity.class));
                break;

            //Chama a tela de cadastro
            case R.id.button_Cadastrar:
                startActivity(new Intent(this, CadastroActivity.class));
                break;
        }
    }

    /*
        authState(): Este método é o responsável por vigiar o estado da autenticação.
     */
    private void authState(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Toast.makeText(getBaseContext(), "Usuario " + user.getEmail() + " está logado", Toast.LENGTH_LONG);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        //No start da activity, devemos adicionar o "ouvinte" da autenticação.
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //E não devemos de esquecer de removê-lo no ciclo de onStop, senão o metódo ficará chamando a API do Firebase, podendo exceder os limites de chamadas.
        if(authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }
}