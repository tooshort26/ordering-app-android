package com.e.maiplaceapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.ICart;
import com.e.maiplaceapp.Adapters.CartAdapter;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.CustomerCartResponse;
import com.e.maiplaceapp.Models.FoodResponse;
import com.e.maiplaceapp.Services.Service;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter foodAdapter;
    private LinearLayoutManager layoutManager;

    private List<FoodResponse> customerCartResponseList = new ArrayList<>();

    private int totalCost = 0;
    private Button btnSubmitOrder;

    private ProgressDialog progressDialog;



    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSubmitOrder = view.findViewById(R.id.btnPlaceOrder);

        this.requestItemsInCart(view);

        btnSubmitOrder.setOnClickListener(v -> {
            ((DashboardActivity)getActivity()).placeOrder(customerCartResponseList);
        });
    }

    private void requestItemsInCart(View view) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        int customer_id = SharedPref.getSharedPreferenceInt(getContext(), "customer_id", 0);
        Retrofit retrofit = Service.RetrofitInstance(getContext());
        ICart service    = retrofit.create(ICart.class);

        Call<CustomerCartResponse> customerCartResponseCall = service.getCustomerItemsInCart(customer_id);

        customerCartResponseCall.enqueue(new Callback<CustomerCartResponse>() {
            @Override
            public void onResponse(Call<CustomerCartResponse> call, Response<CustomerCartResponse> response) {
                if(response.isSuccessful()) {
                    customerCartResponseList = response.body().getFoods();

                    foodAdapter = new CartAdapter(customerCartResponseList, getContext());
                    recyclerView = view.findViewById(R.id.cart_recycler_view);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(foodAdapter);

                    for(FoodResponse f : response.body().getFoods()) {
                        totalCost += f.getPrice();
                    }

                    btnSubmitOrder.setText(String.format("TOTAL : %s - SUBMIT ORDER", String.valueOf(totalCost)));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CustomerCartResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }


}

