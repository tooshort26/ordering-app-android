package com.e.maiplaceapp.Models.Orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CustomerOrderFoodResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_no")
    @Expose
    private Integer orderNo;
    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("order_type")
    @Expose
    private String orderType;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("customer")
    @Expose
    private Customer customer;
    @SerializedName("foods")
    @Expose
    private List<Food> foods = null;

    @SerializedName("status")
    @Expose
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}