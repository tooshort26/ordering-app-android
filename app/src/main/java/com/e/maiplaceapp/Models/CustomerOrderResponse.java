package com.e.maiplaceapp.Models;

import com.google.gson.annotations.SerializedName;

public class CustomerOrderResponse {
    @SerializedName("message")
    public String message;

    @SerializedName("code")
    public int code;

    @SerializedName("order_no")
    public String order_no;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
}
