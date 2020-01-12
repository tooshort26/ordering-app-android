package com.e.maiplaceapp.API;

import com.e.maiplaceapp.Models.Cart.CustomerAddCartRequest;
import com.e.maiplaceapp.Models.Cart.CustomerAddCartResponse;
import com.e.maiplaceapp.Models.Cart.CustomerRemoveItemInCartRequest;
import com.e.maiplaceapp.Models.Cart.CustomerRemoveItemInCartResponse;
import com.e.maiplaceapp.Models.CustomerCartResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ICart {
    @GET("customer/cart/{customer_id}")
    Call<CustomerCartResponse> getCustomerItemsInCart(@Path("customer_id") int customer_id);

    @POST("/customer/cart")
    Call<CustomerAddCartResponse> add(@Body CustomerAddCartRequest customerAddCartRequest);

    @POST("/customer/cart/remove/item")
    Call<CustomerRemoveItemInCartResponse> remove(@Body CustomerRemoveItemInCartRequest customerRemoveItemInCartRequest);
}
