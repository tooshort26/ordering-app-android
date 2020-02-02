package com.e.maiplaceapp;


import android.app.ProgressDialog;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.ICart;
import com.e.maiplaceapp.API.ICategory;
import com.e.maiplaceapp.Adapters.CategoryAdapter;
import com.e.maiplaceapp.Dialogs.AddToCartDialog;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.Category.CategoryResponse;
import com.e.maiplaceapp.Models.Category.Food;
import com.e.maiplaceapp.Models.Cart.CustomerAddCartRequest;
import com.e.maiplaceapp.Models.Cart.CustomerAddCartResponse;
import com.e.maiplaceapp.Services.Service;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FoodFragment extends Fragment implements View.OnClickListener, AddToCartDialog.onQuantitySend {
    private static final String TAG = "FoodFragment";

    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    LinearLayoutManager layoutManager;
    LinearLayout foodFragmentLayout;
    List<CategoryResponse> categories = new ArrayList<>();
    int foodId;
    int foodPrice;

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
        int category_id = 0;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category_id = bundle.getInt("category_id", 0);
        }




//        view.findViewById(R.id.btnStoreLocator).setOnClickListener(v -> Toast.makeText(getContext(), "Redirect to the active where it's display the store place.", Toast.LENGTH_SHORT).show());

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


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();



        Call<CategoryResponse> categoryResponseCall = service.getFoodByCategory(category_id);
        categoryResponseCall.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                CategoryResponse category = response.body();

                        TextView categoryName = new TextView(getContext());

                        categoryName.setText(category.getName());
                        categoryName.setTextSize(30);
                        categoryName.setTypeface(typeface);
                        categoryName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        foodFragmentLayout.addView(categoryName);

                        for(Food food: category.getFoods()) {
                            // create a new textview

                            TextView foodTextView       = new TextView(getContext());
                            TextView foodPriceTextView  = new TextView(getContext());
                            ImageView foodImageView     = new ImageView(getContext());
                            Button btnAddToCart         = new Button(getContext());

                            // If the food is remove in the admin stop the render process.
                            if(food.getStatus().equalsIgnoreCase("remove") ) {
                                continue;
                            }


                            // Set the tag of button with food id and price
                            btnAddToCart.setTag(food.getId() + "|" + food.getPrice());

                            foodImageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            foodImageView.setLayoutParams(parms);



                            // Get Food Images
                            Picasso.get().load( "https://mai-place.herokuapp.com" + food.getImages().get(0).getImage()).into(foodImageView, new com.squareup.picasso.Callback() {
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
                            foodPriceTextView.setText(String.format("Price: ₱%s", String.valueOf(food.getPrice()).concat(".00")));
                            foodTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            foodTextView.setText(String.format("%s - %s", food.getName(), food.getDescription()));


                            if(food.getStatus().equalsIgnoreCase("out_of_stock")) {
                                btnAddToCart.setEnabled(false);
                                btnAddToCart.setBackgroundColor(Color.parseColor("#a30000"));
                                btnAddToCart.setTextColor(Color.parseColor("#ffffff"));
                                btnAddToCart.setText("OUT OF STOCK");
                            } else {
                                btnAddToCart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                                btnAddToCart.setTextColor(Color.parseColor("#ffffff"));
                                btnAddToCart.setText("ADD TO CART");
                                btnAddToCart.setOnClickListener(FoodFragment.this);
                            }

                            btnAddToCart.setLayoutParams(addToCartParams);
                            btnAddToCart.setTypeface(typeface);


                            // add the textview to the linearlayout
                            foodFragmentLayout.addView(foodImageView);
                            foodFragmentLayout.addView(foodPriceTextView);
                            foodFragmentLayout.addView(foodTextView);
                            foodFragmentLayout.addView(btnAddToCart);
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });


    }


    // Method for clicking the Add to Cart
    @Override
    public void onClick(View v) {
        String[] splitted = v.getTag().toString().split("\\|");
        foodId = Integer.parseInt(splitted[0]);
        foodPrice = Integer.parseInt(splitted[1]);

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


        Call<CustomerAddCartResponse> customerCartResponseCall = service.add(new CustomerAddCartRequest(customerId,foodId, orderQuantity, foodPrice));

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


}
