package com.e.maiplaceapp.API;


import com.e.maiplaceapp.Models.Category.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ICategory {
    @GET("categories")
    Call<List<CategoryResponse>> get();

    @GET("categories/{category_id}")
    Call<CategoryResponse> getFoodByCategory(@Path("category_id") int category_id);
}
