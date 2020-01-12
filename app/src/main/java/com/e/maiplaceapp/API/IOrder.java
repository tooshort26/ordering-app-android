package com.e.maiplaceapp.API;

import com.e.maiplaceapp.Models.CustomerOrderRequest;
import com.e.maiplaceapp.Models.CustomerOrderResponse;
import com.e.maiplaceapp.Models.Receipt.CustomerReceiptResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IOrder {
    @POST("/customer/order")
    Call<CustomerOrderResponse> placeOrder(@Body CustomerOrderRequest customerOrderRequest);

    @GET("/customer/receipt/{customer_id}/{order_no}")
    Call<CustomerReceiptResponse> getReceipt(@Path("customer_id") int customer_id, @Path("order_no") int order_no);


}
