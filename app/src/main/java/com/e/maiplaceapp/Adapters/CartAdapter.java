package com.e.maiplaceapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.ICart;
import com.e.maiplaceapp.CartFragment;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.Cart.CustomerRemoveItemInCartRequest;
import com.e.maiplaceapp.Models.Cart.CustomerRemoveItemInCartResponse;
import com.e.maiplaceapp.Models.FoodResponse;
import com.e.maiplaceapp.R;
import com.e.maiplaceapp.Services.Service;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {



    public interface onItemRemove {
        void sendItemPrice(double price);
    }


    private List<FoodResponse> cartResponseList;
    private Context context;
    private CartFragment cartFragment;


    public CartAdapter(List<FoodResponse> items, Context context, CartFragment cartFragment) {
        this.cartResponseList = items;
        this.context = context;
        this.cartFragment = cartFragment;
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
        Picasso.get().load( "https://mai-place.herokuapp.com" + cartResponse.getImage()).into(holder.foodImageView);
        holder.txtCartFoodName.setText(String.format("Name         : %s", cartResponse.getName()));
        holder.txtCartFoodQuantity.setText(String.format("Quantity   : %d", cartResponse.getQuantity()));
        holder.txtCartFoodPrice.setText(String.format("Price           : ₱%s0", cartResponse.getPrice()));
//        holder.txtCartCreatedAt.setText(String.format("Add to cart on %s", cartResponse.getCreated_at()));

        int customer_id = SharedPref.getSharedPreferenceInt(this.context, "customer_id", 0);


        if(customer_id != 0) {
            holder.cartItemDelete.setOnClickListener(v -> {
                // Build an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(cartResponse.getName());
                builder.setMessage("Are you sure for removing this item to your cart?");
                // Set the alert dialog yes button click listener
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    Retrofit retrofit = Service.RetrofitInstance(v.getContext());
                    ICart service = retrofit.create(ICart.class);

                    CustomerRemoveItemInCartRequest cartRemoveItem = new CustomerRemoveItemInCartRequest();
                    cartRemoveItem.setCustomer_id(customer_id);
                    cartRemoveItem.setFood_id(cartResponse.getId());
                    Call<CustomerRemoveItemInCartResponse> customerRemoveItemCall = service.remove(cartRemoveItem);

                    customerRemoveItemCall.enqueue(new Callback<CustomerRemoveItemInCartResponse>() {
                        @Override
                        public void onResponse(Call<CustomerRemoveItemInCartResponse> call, Response<CustomerRemoveItemInCartResponse> response) {
                            if  (response.isSuccessful() && response.body().getCode() == 200) {
//                                Toast.makeText(context, "Succesfully remove the item.", Toast.LENGTH_SHORT).show();
                                cartFragment.updateTotalPrice(cartResponse.getPrice());
                                cartResponseList.remove(position);
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<CustomerRemoveItemInCartResponse> call, Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });




                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            });
        } else {

        }




    }

    @Override
    public int getItemCount() {
        return cartResponseList.size();
    }

    class CartHolder extends RecyclerView.ViewHolder {
         TextView txtCartFoodName, txtCartFoodQuantity, txtCartFoodPrice, txtCartCreatedAt;
         ImageView foodImageView;
         Button cartItemDelete;



        public CartHolder(@NonNull View itemView) {
            super(itemView);
            txtCartFoodName = itemView.findViewById(R.id.cart_food_name);
            txtCartCreatedAt = itemView.findViewById(R.id.cart_food_description);
            txtCartFoodQuantity = itemView.findViewById(R.id.cart_food_quantity);
            txtCartFoodPrice = itemView.findViewById(R.id.cart_food_price);
            foodImageView = itemView.findViewById(R.id.cart_food_image);
            cartItemDelete = itemView.findViewById(R.id.cartItemDelete);
         }
    }
}
