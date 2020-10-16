package com.example.shoppinglist;

public class ShoppingList {

    private String itemName;
    private double itemPrice;

    public ShoppingList(String itName, double itPrice) {
        itemName = itName;
        itemPrice = itPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
