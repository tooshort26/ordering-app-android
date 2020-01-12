package com.e.maiplaceapp.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.DashboardActivity;
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
        holder.categoryLayout.setOnClickListener(v -> {
            DashboardActivity activity = (DashboardActivity) holder.txtCategoryName.getContext();
           activity.getSample(category.getId());
        });
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


        public CategoryHolder(View itemView) {
            super(itemView);

            txtCategoryName = itemView.findViewById(R.id.category_name);
            txtCategoryDescription = itemView.findViewById(R.id.category_description);
            categoryLayout = itemView.findViewById(R.id.categoryLayout);
            categoryImageView = itemView.findViewById(R.id.category_image);
        }
    }
}