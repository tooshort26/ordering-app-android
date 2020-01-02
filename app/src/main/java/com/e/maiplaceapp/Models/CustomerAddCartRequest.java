package com.e.maiplaceapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerAddCartRequest {
    @SerializedName("customer_id")
    @Expose
    private int customer_id;

    @SerializedName("food_id")
    @Expose
    private int food_id;

    @SerializedName("quantity")
    @Expose
    private int quantity;


    public CustomerAddCartRequest(int customer_id, int food_id, int quantity) {
        this.setCustomer_id(customer_id);
        this.setFood_id(food_id);
        this.setQuantity(quantity);
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
