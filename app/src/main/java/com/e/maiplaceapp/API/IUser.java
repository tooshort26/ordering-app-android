package com.e.maiplaceapp.API;

import com.e.maiplaceapp.Models.CustomerLoginRequest;
import com.e.maiplaceapp.Models.CustomerLoginResponse;
import com.e.maiplaceapp.Models.CustomerRequest;
import com.e.maiplaceapp.Models.CustomerResponse;
import com.e.maiplaceapp.Models.Profile.CustomerProfileResponse;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileRequest;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IUser {

    @POST("/customer/register")
    Call<CustomerResponse> register(@Body CustomerRequest request);

    @POST("/customer/login")
    Call<CustomerLoginResponse> login(@Body CustomerLoginRequest request);

    @POST("/customer/update/profile")
    Call<CustomerUpdateProfileResponse> update(@Body CustomerUpdateProfileRequest request);

    @GET("/customers/{id}")
    Call<CustomerProfileResponse> getProfile(@Path("id") int customer_id);
}
