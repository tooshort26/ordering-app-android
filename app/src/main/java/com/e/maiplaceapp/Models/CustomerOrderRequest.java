package com.e.maiplaceapp.Models;

public class CustomerOrderRequest {

    public int customer_id;
    public String orders;
    public String order_type;


    public CustomerOrderRequest(int customer_id, String orders, String order_type) {
        this.setCustomer_id(customer_id);
        this.setOrders(orders);
        this.setOrder_type(order_type);
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }
}
