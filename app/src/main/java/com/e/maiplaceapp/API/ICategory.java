package com.e.maiplaceapp.API;


import com.e.maiplaceapp.Models.Category.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ICategory {
    @GET("categories")
    Call<List<CategoryResponse>> get();
}
