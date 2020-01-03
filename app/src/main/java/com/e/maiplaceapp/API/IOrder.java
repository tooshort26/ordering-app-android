package com.e.maiplaceapp.API;

import com.e.maiplaceapp.Models.CustomerOrderRequest;
import com.e.maiplaceapp.Models.CustomerOrderResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IOrder {
    @POST("/customer/order")
    Call<CustomerOrderResponse> placeOrder(@Body CustomerOrderRequest customerOrderRequest);
}
