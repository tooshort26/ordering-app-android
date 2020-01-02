package com.e.maiplaceapp.API;

import com.e.maiplaceapp.Models.CustomerLoginRequest;
import com.e.maiplaceapp.Models.CustomerLoginResponse;
import com.e.maiplaceapp.Models.CustomerRequest;
import com.e.maiplaceapp.Models.CustomerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IUser {

    @POST("/customer/register")
    Call<CustomerResponse> register(@Body CustomerRequest request);

    @POST("/customer/login")
    Call<CustomerLoginResponse> login(@Body CustomerLoginRequest request);
}
