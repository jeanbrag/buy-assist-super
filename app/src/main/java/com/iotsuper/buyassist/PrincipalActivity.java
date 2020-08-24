package com.iotsuper.buyassist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private MyAdapterItems mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db; //Instanciação do banco de dados
    private FirebaseAuth auth; //Variável de Autenticação com
    private GoogleSignInClient googleSignInClient;
    public Data data;

    private FloatingActionButton fabVoice, fabAdd;
    private View emptyView;
    private String email;


    private ArrayList<View> checksSelected;
    private Menu mn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();
        getListFromFirestore();

    }

    /*
        init(): Inicializa as variáveis
     */
    private void init() {
        emptyView = findViewById(R.id.emptyView);

        //Buttons
        fabVoice = (FloatingActionButton) findViewById(R.id.fabVoice);
        fabVoice.setOnClickListener(this);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);

        checksSelected = new ArrayList<View>();

        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.rvProductsLC);
        //recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);

        auth = FirebaseAuth.getInstance(); //Inicializando a variável com a autenticação atual, caso exista
        FirebaseUser user = auth.getCurrentUser(); //Pega o usuário atual
        email = user.getEmail();

        db = FirebaseFirestore.getInstance(); //Inicializando o banco de dados
    }

    /*
        getListFromFirestore(): Nesse método pegamos a lista de produtos do usuário no Banco de Dados
     */
     private void getListFromFirestore() {
        DocumentReference docRef = db.collection("users").document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        data = document.toObject(Data.class);
                        try{
                        setRecyclerView(data.getProdutos());
                        }catch (Exception e){
                            Toast.makeText(getBaseContext(),e.toString(),Toast.LENGTH_LONG).show();
                        }

                    } else {
                            criarDocumentoUsuario(email);
                            getListFromFirestore();
                    }
                } else {
                    Toast.makeText(getBaseContext(),"Erro em recuperar lista", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
        setRecyclerView(): Inicializa o Recycler View com os dados do Firestore
     */
    private void setRecyclerView(ArrayList<Produto> datalist) {

        if(datalist.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }
        else
            emptyView.setVisibility(View.GONE);

        mAdapter = new MyAdapterItems(datalist);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickOnRecyclerView(new ClickOnRecyclerView() {
            @Override
            public void onCustomClick(View view) {
                if(checksSelected.size() == 0) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxProduct);
                    updateCheck(checkBox.getText().toString(), checkBox);
                }
                else {
                    onLongClick(view);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxProduct);
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }

            @Override
            public void onLongClick(View view) {
                if(!checksSelected.contains(view)) {
                    view.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    checksSelected.add(view);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mn.findItem(R.id.deleteAll).setVisible(true);
                }
                else{
                    view.setBackgroundColor(getResources().getColor(R.color.colorText));
                    checksSelected.remove(view);
                    if(checksSelected.size()==0){
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        mn.findItem(R.id.deleteAll).setVisible(false);
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(checksSelected.size()>0)
            backHandler();
        else
            finish();
    }

    private void backHandler() {
        for(View view : checksSelected){
            view.setBackgroundColor(getResources().getColor(R.color.colorText));
        }
        checksSelected.clear();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mn.findItem(R.id.deleteAll).setVisible(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_ITEM && resultCode == Activity.RESULT_OK){
            atualizaListaAdd(data.getStringExtra("item"));

        }

        if(requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            atualizaListaAdd(result.get(0));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        mn = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logOut();
                startActivity(new Intent(this,FirstActivity.class));
                finish();
                break;
            case android.R.id.home:
                backHandler();
                break;
            case R.id.deleteAll:
                deleteHandler();
                break;
            case R.id.limpar:
                db.collection("users").document(email)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getBaseContext(), "Lista vazia!", Toast.LENGTH_LONG).show();
                                getListFromFirestore();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getBaseContext(), "Erro ao limpar lista - Tente Novamente!", Toast.LENGTH_LONG).show();
                            }
                        });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteHandler() {
        for(View view : checksSelected){
            CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxProduct);
            atualizaListaRemove(cb.getText().toString(), cb.isChecked());
        }
        checksSelected.clear();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mn.findItem(R.id.deleteAll).setVisible(false);
    }


    private void updateCheck(String item, CheckBox checkBox) {
        atualizaListaRemove(item, !checkBox.isChecked());
        Produto prod = new Produto(item, checkBox.isChecked());
        DocumentReference lista = db.collection("users").document(email);
        lista.update("produtos", FieldValue.arrayUnion(prod));
    }

    private void atualizaListaAdd(String item) {
        Produto prod = new Produto(item, false);
        DocumentReference lista = db.collection("users").document(email);
        lista.update("produtos", FieldValue.arrayUnion(prod));
        getListFromFirestore();
}

    private void atualizaListaRemove(String item, boolean bool) {
        Produto prod = new Produto(item, bool);
        DocumentReference lista = db.collection("users").document(email);
        lista.update("produtos", FieldValue.arrayRemove(prod));
        getListFromFirestore();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAdd:
                addItem();
                break;
            case R.id.fabVoice:
                addItemVoice();
                break;
        }
    }

    private void addItem(){
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivityForResult(intent, ADD_ITEM);
    }

    private void addItemVoice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale o produto!");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception ex){
            Toast.makeText(this,"Houve uma exceção: " + ex.toString(), Toast.LENGTH_LONG);
        }
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
                        //Toast.makeText(getBaseContext(), "bd criado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        atualizaListaAdd("Teste");
        atualizaListaRemove("Teste", false);
    }

    private void logOut() {
        //LogOut caso tenha sido feito Login com Email + Senha
        FirebaseAuth.getInstance().signOut();

        //LogOut caso tenha sido feito Login com o Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
    }

    private static int SPEECH_REQUEST_CODE = 0;
    private static int ADD_ITEM = 120;
}