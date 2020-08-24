package com.iotsuper.buyassist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddItemActivity extends AppCompatActivity implements View.OnClickListener{

    private Button addButton;
    private EditText input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        input = (EditText) findViewById(R.id.edtNameInput);

        addButton = (Button) findViewById(R.id.btnAdd);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:
                String item = input.getText().toString();
                if(!item.isEmpty()){
                    Intent intent = new Intent();
                    intent.putExtra("item", item);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else{
                    Toast.makeText(this, "Insira um produto", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }
}