package com.e.maiplaceapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.Models.FoodResponse;
import com.e.maiplaceapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {


    private List<FoodResponse> cartResponseList;
    private Context context;

    public CartAdapter(List<FoodResponse> items, Context context) {
        this.cartResponseList = items;
        this.context = context;
    }


    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_recyclerview_layout, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        FoodResponse cartResponse = cartResponseList.get(position);
        // Get Food Images
        Picasso.get().load( "http://192.168.1.10:8000"+ cartResponse.getImage()).into(holder.foodImageView);
        holder.txtCartFoodName.setText(String.format("%s - PHP: %s.00", cartResponse.getName(), String.valueOf(cartResponse.getPrice())));
    }

    @Override
    public int getItemCount() {
        return cartResponseList.size();
    }

    class CartHolder extends RecyclerView.ViewHolder {
         TextView txtCartFoodName;
         ImageView foodImageView;



        public CartHolder(@NonNull View itemView) {
            super(itemView);
            txtCartFoodName = itemView.findViewById(R.id.cart_food_name);
            foodImageView = itemView.findViewById(R.id.cart_food_image);
         }
    }
}
