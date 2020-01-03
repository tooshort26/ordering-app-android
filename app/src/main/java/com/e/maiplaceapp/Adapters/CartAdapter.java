package com.e.maiplaceapp.Adapters;

import android.annotation.SuppressLint;
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        FoodResponse cartResponse = cartResponseList.get(position);
        Picasso.get().load( "http://192.168.1.10:8000"+ cartResponse.getImage()).into(holder.foodImageView);
        holder.txtCartFoodName.setText(String.format("Name : %s", cartResponse.getName()));
        holder.txtCartFoodQuantity.setText(String.format("Quantity : %d", cartResponse.getQuantity()));
        holder.txtCartFoodPrice.setText(String.format("Price : %s0", cartResponse.getPrice()));
//        holder.txtCartCreatedAt.setText(String.format("Add to cart on %s", cartResponse.getCreated_at()));
    }

    @Override
    public int getItemCount() {
        return cartResponseList.size();
    }

    class CartHolder extends RecyclerView.ViewHolder {
         TextView txtCartFoodName, txtCartFoodQuantity, txtCartFoodPrice, txtCartCreatedAt;
         ImageView foodImageView;



        public CartHolder(@NonNull View itemView) {
            super(itemView);
            txtCartFoodName = itemView.findViewById(R.id.cart_food_name);
            txtCartCreatedAt = itemView.findViewById(R.id.cart_food_description);
            txtCartFoodQuantity = itemView.findViewById(R.id.cart_food_quantity);
            txtCartFoodPrice = itemView.findViewById(R.id.cart_food_price);
            foodImageView = itemView.findViewById(R.id.cart_food_image);
         }
    }
}
