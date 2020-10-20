package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.shoppinglist.MainActivity.MESSAGE_KEY;
import static com.example.shoppinglist.Shopping.MESSAGE_KEY2;

public class CheckOut extends AppCompatActivity {

    private static final String TAG = "Checkout";
    ArrayList<ShoppingList> shoppingLists;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        shoppingLists = new ArrayList<>();

        Intent i = getIntent();
        String items = i.getStringExtra(MESSAGE_KEY);
        TextView totalItems = (TextView) findViewById(R.id.totalItems);
        totalItems.setText(items);

        String price = i.getStringExtra(MESSAGE_KEY2);
        TextView totalPrice = (TextView) findViewById(R.id.totalCost);
        totalPrice.setText("€" + price);

        Button pay = (Button) findViewById(R.id.pay_button);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CheckOut.this, "Your order has been confirmed", Toast.LENGTH_LONG).show();
                Intent i = new Intent(CheckOut.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
