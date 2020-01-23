package com.e.maiplaceapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.IOrder;
import com.e.maiplaceapp.Adapters.TrackOrderAdapter;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.Orders.CustomerOrderFoodResponse;
import com.e.maiplaceapp.Services.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackOrderFragment extends Fragment {

    private ProgressDialog progressDialog;


    private RecyclerView recyclerView;
    private TrackOrderAdapter trackOrderAdapter;
    private LinearLayoutManager layoutManager;


    public TrackOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getCustomerOrders(view);

    }

    private void getCustomerOrders(@NonNull View view) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting all your orders please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        int customer_id = SharedPref.getSharedPreferenceInt(getContext(), "customer_id", 0);

        Retrofit retrofit = Service.RetrofitInstance(getContext());
        IOrder service = retrofit.create(IOrder.class);

        Call<List<CustomerOrderFoodResponse>> customerOrderFoodResponse = service.getOrders(customer_id);
        customerOrderFoodResponse.enqueue(new Callback<List<CustomerOrderFoodResponse>>() {
            @Override
            public void onResponse(Call<List<CustomerOrderFoodResponse>> call, Response<List<CustomerOrderFoodResponse>> response) {
                if(response.isSuccessful() && response.code() == 200) {
                    progressDialog.dismiss();


                    trackOrderAdapter = new TrackOrderAdapter(response.body(), getContext());
                    recyclerView = view.findViewById(R.id.track_orders_recycler_view);
//                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(trackOrderAdapter);

                }
            }

            @Override
            public void onFailure(Call<List<CustomerOrderFoodResponse>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
