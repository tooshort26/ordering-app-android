package com.e.maiplaceapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.ICart;
import com.e.maiplaceapp.API.IOrder;
import com.e.maiplaceapp.Adapters.CartAdapter;
import com.e.maiplaceapp.Dialogs.DeliverTypeDialog;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.CustomerCartResponse;
import com.e.maiplaceapp.Models.CustomerOrderRequest;
import com.e.maiplaceapp.Models.CustomerOrderResponse;
import com.e.maiplaceapp.Models.FoodResponse;
import com.e.maiplaceapp.Services.Service;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CartFragment extends Fragment implements  DeliverTypeDialog.onSelectTypeSend {
    private static final String TAG = "CartFragment";
    private RecyclerView recyclerView;
    private CartAdapter foodAdapter;
    private LinearLayoutManager layoutManager;

    private List<FoodResponse> customerCartResponseList = new ArrayList<>();

    public static int totalCost;
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
        totalCost = 0;

        this.requestItemsInCart(view);


        view.findViewById(R.id.btnOptions).setOnClickListener(v -> {
            // Display the dialog for deliver type.
            DeliverTypeDialog deliverTypeDialog = new DeliverTypeDialog();
            deliverTypeDialog.setTargetFragment(CartFragment.this, 2);
            deliverTypeDialog.show(getFragmentManager(), "DeliverTypeDialog");
        });


        btnSubmitOrder.setOnClickListener(v -> {


            if(customerCartResponseList.size() == 0) {
                Toast.makeText(getContext(), "Please add an item first.", Toast.LENGTH_SHORT).show();
            } else {

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Generating Receipt please wait..");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                Gson gson = new Gson();
                int customer_id = SharedPref.getSharedPreferenceInt(getContext(), "customer_id", 0);
                String orders = gson.toJson(customerCartResponseList);
                String order_type = SharedPref.getSharedPreferenceString(getContext(),"selected_type", "deliver");


                // Is the user active.
                if(customer_id != 0) {
                    Retrofit retrofit = Service.RetrofitInstance(getContext());
                    IOrder service = retrofit.create(IOrder.class);

                    Call<CustomerOrderResponse> customerOrderResponseCall = service.placeOrder(new CustomerOrderRequest(customer_id, orders, order_type));
                    customerOrderResponseCall.enqueue(new Callback<CustomerOrderResponse>() {
                        @Override
                        public void onResponse(Call<CustomerOrderResponse> call, Response<CustomerOrderResponse> response) {
                            if(response.isSuccessful() && response.body().getCode() == 201) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),  response.body().getMessage() + " with no : " + response.body().getOrder_no(), Toast.LENGTH_SHORT).show();
                                // Replace the current fragment by new Fragment.
                                Bundle bundle = new Bundle();
                                ReceiptFragment receiptFragment = new ReceiptFragment();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                bundle.putString("order_no", response.body().getOrder_no());
                                fragmentTransaction.replace(R.id.flContent, receiptFragment);
                                receiptFragment.setArguments(bundle);
                                fragmentTransaction.commit();
                            }
                        }

                        @Override
                        public void onFailure(Call<CustomerOrderResponse> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }


            }





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
                    foodAdapter = new CartAdapter(customerCartResponseList, getContext(), CartFragment.this);
                    recyclerView = view.findViewById(R.id.cart_recycler_view);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//                    layoutManager = new LinearLayoutManager(getContext());
//                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(foodAdapter);

                    for(FoodResponse f : customerCartResponseList) {
                        Log.d(TAG, "TOTAL_COST" + f.getPrice());
                        totalCost += f.getPrice();
                    }

                    btnSubmitOrder.setText(String.format("TOTAL : ₱%s.00 - SUBMIT ORDER", String.valueOf(totalCost)));
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

    // this method is for after deletion of the item in the cart need to update the total price.
    public void updateTotalPrice(Double price)
    {
        totalCost = (int) (totalCost - price);
        btnSubmitOrder.setText(String.format("TOTAL : ₱%s.00 - SUBMIT ORDER", String.valueOf(totalCost)));

    }


    @Override
    public void sendType(String type) {
        // Save the selected Type.
        SharedPref.setSharedPreferenceString(getContext(),"selected_type", type);
        Toast.makeText(getContext(), "You select : " + type, Toast.LENGTH_SHORT).show();
    }


}

