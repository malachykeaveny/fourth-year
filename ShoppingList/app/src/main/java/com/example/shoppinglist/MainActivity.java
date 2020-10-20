package com.example.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE_KEY="Hello ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bton= (Button) findViewById(R.id.loginBtn);
        bton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTxtname = (EditText) findViewById(R.id.username);
                EditText editPwname = (EditText) findViewById(R.id.password);
                //if (editTxtname.getText().toString().equals("mal") && editPwname.getText().toString().equals("gg")) {
                if (editTxtname.getText().toString().length() >= 6 && editPwname.getText().toString().length() >= 6 && !editPwname.getText().toString().contains(" ")) {
                    Intent intent = new Intent(MainActivity.this, Shopping.class);
                    String name = editTxtname.getText().toString();
                    intent.putExtra(MESSAGE_KEY, name);
                    startActivity(intent);
                } else {
                    Snackbar mySnackbar = Snackbar.make(v, "Wrong credentials", BaseTransientBottomBar.LENGTH_SHORT);
                    mySnackbar.show();
                    EditText editTxtname1 = (EditText) findViewById(R.id.username);
                    editTxtname1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
            }});
    }
}