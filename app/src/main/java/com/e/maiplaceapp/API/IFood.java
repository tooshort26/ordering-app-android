package com.e.maiplaceapp.API;

import com.e.maiplaceapp.Models.FoodResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IFood {
    @GET("foods")
    Call<List<FoodResponse>> getByCategoryId(@Query("category_id") int category_id);
}
