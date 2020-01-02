package com.e.maiplaceapp.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.Models.FoodResponse;
import com.e.maiplaceapp.R;

import java.util.ArrayList;
import java.util.List;


public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHolder> {

    private List<FoodResponse> foods;
    private Context context;

    public FoodAdapter(List<FoodResponse> foods, Context context) {
        this.foods = foods;
        this.context = context;
    }

    @Override
    public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_recyclerview_layout, parent, false);
        return new FoodHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodHolder holder, int position) {
        FoodResponse food = foods.get(position);
        // Get and set the first image of food.
//        Picasso.get().load(food.getImage().get(0)).into(holder.imgFood);
        holder.txtFoodName.setText(food.getName());
        holder.txtFoodDescription.setText(food.getDescription());
        holder.txtFoodPrice.setText(String.format("PHP: %s0", food.getPrice()));

        holder.btnAddToCart.setOnClickListener(v -> this.addToCartRequest(food));
    }

    private void addToCartRequest(FoodResponse food) {
//        ProgressDialog progressDialog = new ProgressDialog(this.context);
//        progressDialog.setMessage("Please wait...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.show();
//
//        Retrofit retrofit = Service.RetrofitInstance(this.context);
//        ICart service    = retrofit.create(ICart.class);
//
////        CustomerAddCartRequest addCartRequest = new CustomerAddCartRequest();
//        int customer_id = SharedPref.getSharedPreferenceInt(this.context,"customer_id", 0);
//
//        addCartRequest.setCustomer_id(customer_id);
//        addCartRequest.setFood_id(food.getId());
//
//        Call<CustomerAddCartResponse> customerAddCartResponseCall = service.add(addCartRequest);
//
//        customerAddCartResponseCall.enqueue(new Callback<CustomerAddCartResponse>() {
//            @Override
//            public void onResponse(Call<CustomerAddCartResponse> call, Response<CustomerAddCartResponse> response) {
//                if(response.isSuccessful()) {
//                    progressDialog.dismiss();
//                    Toast.makeText(context, "Successfully add to cart.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CustomerAddCartResponse> call, Throwable t) {
//                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return foods.size();
    }

    public void filterList(ArrayList<FoodResponse> filteredList) {
        foods = filteredList;
        notifyDataSetChanged();
    }

    class FoodHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtFoodName;
        TextView txtFoodDescription;
        TextView txtFoodPrice;
        Button btnAddToCart;



        public FoodHolder(View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.food_image);
            txtFoodName = itemView.findViewById(R.id.food_name);
            txtFoodDescription = itemView.findViewById(R.id.food_description);
            txtFoodPrice = itemView.findViewById(R.id.food_price);
            btnAddToCart = itemView.findViewById(R.id.btnFoodAddToCart);
        }
    }
}