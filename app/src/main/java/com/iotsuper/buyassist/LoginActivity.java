package com.iotsuper.buyassist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText_EmailLogin, editText_SenhaLogin;

    private Button button_Ok, button_Recuperar;

    //Botão de Login com o Google feito com CardView
    private CardView loginGoogle;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_EmailLogin = (EditText) findViewById(R.id.editText_EmailLogin);
        editText_SenhaLogin = (EditText) findViewById(R.id.editText_SenhaLogin);

        button_Ok = (Button) findViewById(R.id.button_Ok);
        button_Recuperar = (Button) findViewById(R.id.button_Recuperar);
        loginGoogle = (CardView) findViewById(R.id.loginGoogle);

        button_Ok.setOnClickListener(this);
        button_Recuperar.setOnClickListener(this);
        loginGoogle.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        servicosGoogle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_Ok:
                loginEmail();
                break;

            case R.id.button_Recuperar:
                String email = editText_EmailLogin.getText().toString().trim();
                if(!email.isEmpty())
                    recuperarSenha(email);
                else Toast.makeText(getBaseContext(), "Insira o email para recuperação",Toast.LENGTH_LONG).show();
                break;
            case R.id.loginGoogle:
                signInGoogle();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 555){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginComOGoogle(account.getIdToken());
            }catch (ApiException e){
                Toast.makeText(getBaseContext(), "Erro ao logar", Toast.LENGTH_LONG).show();
            }

        }
    }

    //-------------------------SERVIÇOS LOGIN EMAIL--------------------------------
    private void loginEmail(){
        String email = editText_EmailLogin.getText().toString().trim();
        String senha = editText_SenhaLogin.getText().toString();

        if(email.isEmpty() || senha.isEmpty() )
            Toast.makeText(getBaseContext(), "Insira os campos obrigatórios", Toast.LENGTH_LONG).show();
        else {
            if(Utility.statusInternet_MoWi( getBaseContext() ))
                confirmarLoginEmail(email,senha);
            else Toast.makeText(getBaseContext(), "Erro - Sem conexão com a Internet", Toast.LENGTH_LONG).show();
        }
    }

    private void confirmarLoginEmail(String email, String senha){
        auth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getBaseContext(),PrincipalActivity.class));
                    Toast.makeText(getBaseContext(), "Usuário logado com sucesso", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    String resposta = task.getException().toString();
                    Utility.opcoesErro(getBaseContext(), resposta);
                }
            }
        });
    }

    private void recuperarSenha(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getBaseContext(), "Email de Recuperação enviado!", Toast.LENGTH_LONG).show();
                }
                else {
                    String resposta = task.getException().toString();
                    Utility.opcoesErro(getBaseContext(), resposta);
                }
            }
        });
    }

    //------------------SERVIÇOS LOGIN GOOGLE ---------------------------------------------------------

    private void servicosGoogle(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInGoogle(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account == null){
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 555);

        }
        //ja existe alguem conectado
        else {
            Toast.makeText(getBaseContext(), "Já logado", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getBaseContext(),PrincipalActivity.class));
            finish();

        }
    }

    private void loginComOGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(getBaseContext(),PrincipalActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), "Erro ao logar", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}