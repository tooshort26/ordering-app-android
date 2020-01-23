package com.e.maiplaceapp.Models.Orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Food {

    @SerializedName("order_order_no")
    @Expose
    private Integer orderOrderNo;
    @SerializedName("food_id")
    @Expose
    private Integer foodId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_food")
    @Expose
    private List<OrderFood> orderFood = null;

    public Integer getOrderOrderNo() {
        return orderOrderNo;
    }

    public void setOrderOrderNo(Integer orderOrderNo) {
        this.orderOrderNo = orderOrderNo;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<OrderFood> getOrderFood() {
        return orderFood;
    }

    public void setOrderFood(List<OrderFood> orderFood) {
        this.orderFood = orderFood;
    }

}
