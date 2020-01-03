package com.e.maiplaceapp;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.ICart;
import com.e.maiplaceapp.API.ICategory;
import com.e.maiplaceapp.Adapters.CategoryAdapter;
import com.e.maiplaceapp.Dialogs.AddToCartDialog;
import com.e.maiplaceapp.Dialogs.DeliverTypeDialog;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.Category.CategoryResponse;
import com.e.maiplaceapp.Models.Category.Food;
import com.e.maiplaceapp.Models.CustomerAddCartRequest;
import com.e.maiplaceapp.Models.CustomerAddCartResponse;
import com.e.maiplaceapp.Services.Service;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FoodFragment extends Fragment implements View.OnClickListener, AddToCartDialog.onQuantitySend, DeliverTypeDialog.onSelectTypeSend {
    private static final String TAG = "FoodFragment";

    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    LinearLayoutManager layoutManager;
    LinearLayout foodFragmentLayout;
    List<CategoryResponse> categories = new ArrayList<>();
    int foodId;

    private ProgressDialog progressDialog;


    public FoodFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Retrofit retrofit = Service.RetrofitInstance(getContext());
        ICategory service    = retrofit.create(ICategory.class);


        view.findViewById(R.id.btnOptions).setOnClickListener(v -> {
            // Display the dialog for deliver type.
            DeliverTypeDialog deliverTypeDialog = new DeliverTypeDialog();
            deliverTypeDialog.setTargetFragment(FoodFragment.this, 2);
            deliverTypeDialog.show(getFragmentManager(), "DeliverTypeDialog");
        });

        view.findViewById(R.id.btnStoreLocator).setOnClickListener(v -> Toast.makeText(getContext(), "Redirect to the active where it's display the store place.", Toast.LENGTH_SHORT).show());

        int width = 450;
        int height = 450;
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
        parms.gravity = Gravity.CENTER_HORIZONTAL;
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.product_sans);

        LinearLayout.LayoutParams addToCartParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        addToCartParams.setMargins(20, 20, 20, 20);
        parms.gravity = Gravity.CENTER_HORIZONTAL;

        foodFragmentLayout = view.findViewById(R.id.foodFragmentLayout);


        Call<List<CategoryResponse>> categoryResponseCall = service.get();
        categoryResponseCall.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                categories = response.body();
                for(CategoryResponse category: categories)
                {
                    TextView categoryName = new TextView(getContext());

                    categoryName.setText(category.getName());
                    categoryName.setTextSize(25);
                    categoryName.setTypeface(typeface);

                    foodFragmentLayout.addView(categoryName);

                       for(Food food: category.getFoods()) {
                        // create a new textview

                        TextView foodTextView = new TextView(getContext());
                        TextView foodPriceTextView = new TextView(getContext());
                        ImageView foodImageView = new ImageView(getContext());
                        Button btnAddToCart = new Button(getContext());

                        btnAddToCart.setTag(food.getId());

                        foodImageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        foodImageView.setLayoutParams(parms);


                        // Get Food Images
                        Picasso.get().load( "http://192.168.1.10:8000"+ food.getImages().get(0).getImage()).into(foodImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("IMAGE_LOAD_SUCCESS", food.getImages().get(0).getImage());
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d("IMAGE_LOAD_ERROR", food.getImages().get(0).getImage() + " CAUSE : " + e.getMessage());
                            }
                        });

                        foodPriceTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        foodPriceTextView.setTypeface(Typeface.DEFAULT_BOLD);
                        foodPriceTextView.setText(String.format("PHP: %s", String.valueOf(food.getPrice()).concat(".00")));
                        foodTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        foodTextView.setText(String.format("%s - %s", food.getName(), food.getDescription()));
                        btnAddToCart.setText("ADD TO CART");
                        btnAddToCart.setLayoutParams(addToCartParams);
                        btnAddToCart.setTypeface(typeface);
                        btnAddToCart.setOnClickListener(FoodFragment.this);


                        // add the textview to the linearlayout
                        foodFragmentLayout.addView(foodImageView);
                        foodFragmentLayout.addView(foodPriceTextView);
                        foodFragmentLayout.addView(foodTextView);
                        foodFragmentLayout.addView(btnAddToCart);
                    }

                }
//
//                categoryAdapter = new CategoryAdapter(categories, getContext());
//
//                recyclerView = view.findViewById(R.id.food_recycler_view);
//
//                layoutManager = new LinearLayoutManager(getContext());
//
//                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//                recyclerView.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {

            }
        });
   /*     EditText searchField = view.findViewById(R.id.searchField);
        this.requestFoodsByCategory(categoryId, view);
        // After building the recyclerview we init the function of searchfield.
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
*/
    }

    //TODO Save the categories first in DB then perform search.
    private void filter(String text) {
//        ArrayList<FoodResponse> filteredList = new ArrayList<>();
//
//        for (FoodResponse item : foods) {
//            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
//                filteredList.add(item);
//            }
//        }
//
//        foodAdapter.filterList(filteredList);
    }

    private void requestFoodsByCategory(int category_id, View view) {

        /*progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Retrofit retrofit = Service.RetrofitInstance(getContext());
        IFood service    = retrofit.create(IFood.class);

        Call<List<FoodResponse>> foodResponseCall = service.getByCategoryId(category_id);
        foodResponseCall.enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if(response.isSuccessful()) {
                    foods = response.body();

                    foodAdapter = new FoodAdapter(foods, getContext());

                    recyclerView = view.findViewById(R.id.food_recycler_view);

                    layoutManager = new LinearLayoutManager(getContext());

                    recyclerView.setLayoutManager(layoutManager);

                    recyclerView.setAdapter(foodAdapter);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    // Method for clicking the Add to Cart
    @Override
    public void onClick(View v) {
        foodId = Integer.parseInt(v.getTag().toString());
        AddToCartDialog addToCartDialog = new AddToCartDialog();
        addToCartDialog.setTargetFragment(FoodFragment.this, 1);
        addToCartDialog.show(getFragmentManager(), "AddToCartDialog");
    }


    @Override
    public void sendQuantity(String quantity) {
        Log.d(TAG, "sendQuantity: Found incoming quantity");

        Retrofit retrofit = Service.RetrofitInstance(getContext());
        ICart service = retrofit.create(ICart.class);


        int customerId = SharedPref.getSharedPreferenceInt(getContext(),"customer_id", 0);
        int orderQuantity = Integer.parseInt(quantity);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        Call<CustomerAddCartResponse> customerCartResponseCall = service.add(new CustomerAddCartRequest(customerId,foodId, orderQuantity));

        customerCartResponseCall.enqueue(new Callback<CustomerAddCartResponse>() {
            @Override
            public void onResponse(Call<CustomerAddCartResponse> call, Response<CustomerAddCartResponse> response) {
                CustomerAddCartResponse addCartResponse = response.body();
                if  (addCartResponse.getCode() == 201 && response.code() == 200) {
                    Toast.makeText(getContext(), "Successfully add to your cart.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CustomerAddCartResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void sendType(String type) {
        // Save the selected Type.
        SharedPref.setSharedPreferenceString(getContext(),"selected_type", type);
        Toast.makeText(getContext(), "You select : " + type, Toast.LENGTH_SHORT).show();
    }
}
