package com.example.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;

import static com.example.shoppinglist.MainActivity.MESSAGE_KEY;

public class Shopping extends AppCompatActivity {

    private static final String TAG = "Shopping";
    ArrayList<ShoppingList> shoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping);
        Intent intent = getIntent();
        String name = intent.getStringExtra(MESSAGE_KEY);
        TextView txtView = (TextView) findViewById(R.id.textView2);
        txtView.setText("Logged in as " + name);
        shoppingList = new ArrayList<>();

        Button cOutBton= (Button) findViewById(R.id.checkOutBtn);
        cOutBton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Shopping.this, CheckOutActivity.class);
                startActivity(i);
            }
        }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addToList) {
            addItem();
        }

        if (id == R.id.removeFromList) {
            removeItem();
        }

        if (id == R.id.viewList) {
            viewAllItems();
        }

        return true;
    }

    private void addItem() {
        Log.d(TAG, "made it this far");

        AlertDialog.Builder builder = new AlertDialog.Builder(Shopping.this);
        builder.setTitle("Add a new item");

        View view = LayoutInflater.from(Shopping.this).inflate(R.layout.add_item_box, null, false);

        builder.setView(view);
        //builder.setView(R.layout.add_item_box);
        final EditText itemName = view.findViewById(R.id.itemName);
        final EditText itemPrice = view.findViewById(R.id.itemPrice);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (itemName.getText().toString().isEmpty()) {
                    itemName.setError("ERROR: Add item name!");
                }
                else if (itemPrice.getText().toString().isEmpty()) {
                    itemPrice.setError("ERROR: ADD item price!");
                }
                else {
                    shoppingList.add(new ShoppingList(itemName.getText().toString(), Double.parseDouble(itemPrice.getText().toString())));
                    Log.d(TAG, "onClick: Shopping list: " + shoppingList.get(0).getItemName() + shoppingList.get(0).getItemPrice());
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    public void removeItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Shopping.this);
        builder.setTitle("Remove an item");

        View view = LayoutInflater.from(Shopping.this).inflate(R.layout.remove_item_box, null, false);

        builder.setView(view);
        //builder.setView(R.layout.add_item_box);
        final EditText removeName = view.findViewById(R.id.removeNameInput);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ShoppingList itemToDelete = null;
                for (ShoppingList sL: shoppingList) {
                    Log.d(TAG, "arraylist: " + sL.getItemName());
                    Log.d(TAG, "Inputted text: " + removeName.getText().toString());

                    if (sL.getItemName().equalsIgnoreCase(removeName.getText().toString())) {
                        itemToDelete = sL;
                        //Toast.makeText(Shopping.this, sL.getItemName() + "deleted!", Toast.LENGTH_LONG).show();
                    }
                }

                if (itemToDelete == null) {
                    Toast.makeText(Shopping.this, "No such item found!", Toast.LENGTH_LONG).show();
                }
                else {
                    shoppingList.remove(itemToDelete);
                    Toast.makeText(Shopping.this, itemToDelete.getItemName() + " deleted!", Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.show();
    }

    public void viewAllItems() {
        String list = "";

        for (ShoppingList sL: shoppingList) {
            list += sL.getItemName().toString() + ": €" + sL.getItemPrice() + " \n";
        }

        Toast.makeText(Shopping.this, list, Toast.LENGTH_LONG).show();
    }


}
