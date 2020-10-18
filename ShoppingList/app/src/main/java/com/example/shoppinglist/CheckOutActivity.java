package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CheckOutActivity extends AppCompatActivity {

    ArrayList<ShoppingList> shoppingList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        Intent intent = getIntent();

        TextView ttlItems= (TextView) findViewById(R.id.totalItems);
        ttlItems.setText("Hi");

    }
}