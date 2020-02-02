package com.e.maiplaceapp.Models.Cart;

public class CustomerEditItemInCartRequest {
    public int customer_id;
    public int food_id;
    public int quantity;
    public Double price;

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public void setPrice(Double food_price) {
        this.price = food_price;
    }
}
