package com.iotsuper.buyassist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
    TELA DE ABERTURA - SPLASH SCREEN
 */
public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth; //Variável de Autenticação com Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance(); //Inicializando a variável com a autenticação atual, caso exista

        getSupportActionBar().hide(); //Oculta a *Support Action Bar*

        temporizador();
    }

    /*
       temporizador(): Esse método é responsável pelo tempo em que a Splash Screen ficará ativa. (3 segundos)
    */
    private void temporizador() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser user = auth.getCurrentUser(); //Pega o usuário atual, caso exista.

                /*Senão existir nenhum usuário autenticado, iremos para a tela inicial do aplicativo (Login e Cadastro),
                caso contrário, iremos para a tela principal do aplicativo (Mapas).
                */
                if(user == null)
                    startActivity(new Intent(getBaseContext(), FirstActivity.class));
                else
                    startActivity(new Intent(getBaseContext(), PrincipalActivity.class));

                finish(); //Finaliza a Splash Screen.
            }
        }, 3000);
    }
}