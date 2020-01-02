package com.e.maiplaceapp.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.Models.Category.CategoryResponse;
import com.e.maiplaceapp.R;

import java.util.ArrayList;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private List<CategoryResponse> categories;
    private Context context;

    public CategoryAdapter(List<CategoryResponse> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recyclerview_layout, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        CategoryResponse category = categories.get(position);
        holder.txtCategoryName.setText(category.getName());
//        holder.txtCategoryDescription.setText(category.getDescription());
    /*    for(Food food: category.getFoods()) {
            // create a new textview
            final TextView foodTextView = new TextView(this.context);
            final TextView foodPriceTextView = new TextView(this.context);
            final ImageView foodImageView = new ImageView(this.context);
            final Button btnAddToCart = new Button(this.context);

            // Add values to elements
            Picasso.get().load( "http://192.168.1.3:8000"+ food.getImages().get(0).getImage()).into(foodImageView);
            Log.d("http://192.168.1.3:8000"+ food.getImages().get(0).getImage(), "TESTING");
            foodPriceTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            foodPriceTextView.setTypeface(Typeface.DEFAULT_BOLD);
            foodPriceTextView.setText(String.format("PHP: %s", String.valueOf(food.getPrice()).concat(".00")));
            foodTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            foodTextView.setText(String.format("%s - %s", food.getName(), food.getDescription()));
            btnAddToCart.setText("ADD TO CART");

            // add the textview to the linearlayout
            holder.foodLayout.addView(foodImageView);
            holder.foodLayout.addView(foodPriceTextView);
            holder.foodLayout.addView(foodTextView);
            holder.foodLayout.addView(btnAddToCart);
        }*/
    }




    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void filterList(ArrayList<CategoryResponse> filteredList) {
        categories = filteredList;
        notifyDataSetChanged();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView categoryImageView;
        TextView txtCategoryName;
        TextView txtCategoryDescription;
        RelativeLayout categoryLayout;
        LinearLayout foodLayout;


        public CategoryHolder(View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.category_name);
            txtCategoryDescription = itemView.findViewById(R.id.category_description);
            categoryLayout = itemView.findViewById(R.id.categoryLayout);
            categoryImageView = itemView.findViewById(R.id.category_image);
            foodLayout = itemView.findViewById(R.id.foodLayout);
        }
    }
}