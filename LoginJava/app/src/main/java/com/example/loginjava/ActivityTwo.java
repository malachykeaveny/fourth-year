package com.example.loginjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.loginjava.MainActivity.MESSAGE_KEY;

public class ActivityTwo extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_two);
        Intent intent= getIntent();
        String name = intent.getStringExtra(MESSAGE_KEY);
        TextView txtView= (TextView) findViewById(R.id.textView2);
        txtView.setText("Hey " + name);

        Button bbton= (Button) findViewById(R.id.backBtn);
        bbton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityTwo.this.finish();
                }
        });
    }
}
