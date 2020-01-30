package com.e.maiplaceapp.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.IOrder;
import com.e.maiplaceapp.Helpers.Strings;
import com.e.maiplaceapp.Models.Orders.CancelOrderRequest;
import com.e.maiplaceapp.Models.Orders.CancelOrderResponse;
import com.e.maiplaceapp.Models.Orders.CustomerOrderFoodResponse;
import com.e.maiplaceapp.R;
import com.e.maiplaceapp.ReceiptFragment;
import com.e.maiplaceapp.Services.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.TrackOrderHolder> {

    private static final String TAG = "TrackOrderAdapter";
    private boolean isOrderCanCancel = false;
    private List<CustomerOrderFoodResponse> orders;
    private Context context;
    private ProgressDialog progressDialog;

    public TrackOrderAdapter(List<CustomerOrderFoodResponse> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public TrackOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_orders_recyclerview_layout, parent, false);
        return new TrackOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrderHolder holder, int position) {
        CustomerOrderFoodResponse orderFoodResponse = orders.get(position);
        holder.orderNo.setText(String.format("Order #         : %s", orderFoodResponse.getOrderNo()));
        holder.orderType.setText(String.format("Order type   : %s", Strings.capitalize(orderFoodResponse.getOrderType())));
        holder.orderAt.setText(String.format("Order Date  : %s", orderFoodResponse.getCreatedAt()));
        holder.viewOrder.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ReceiptFragment receiptFragment = new ReceiptFragment();
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            bundle.putString("order_no", String.valueOf(orderFoodResponse.getOrderNo()));
            receiptFragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContent, receiptFragment).commit();
        });




        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date date = new Date();
            Date d = dateFormat.parse(orderFoodResponse.getCreatedAt());
            if((date.getMinutes() - d.getMinutes()) > 3) {
                Log.d(TAG, "onBindViewHolder: " + " More than 3 minutes");
                holder.cancelOrder.setEnabled(false);
                holder.cancelOrder.setBackgroundColor(Color.parseColor("#E9E9E9"));
                holder.cancelOrder.setTextColor(Color.parseColor("#000000"));
            } else {
                holder.cancelOrder.setEnabled(true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!orderFoodResponse.getStatus().equalsIgnoreCase("incoming")) {
            holder.cancelOrder.setEnabled(false);
            holder.cancelOrder.setBackgroundColor(Color.parseColor("#E9E9E9"));
            holder.cancelOrder.setTextColor(Color.parseColor("#000000"));
        }



        holder.cancelOrder.setOnClickListener(v -> {
            // Build an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Order #: " + orderFoodResponse.getOrderNo());
            builder.setMessage("Are you sure you want to cancel this order?");
            // Set the alert dialog yes button click listener
            builder.setPositiveButton("Yes", (dialog, which) -> {
                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setMessage("Please wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                Retrofit retrofit = Service.RetrofitInstance(this.context);
                IOrder service    = retrofit.create(IOrder.class);

                CancelOrderRequest cancelOrderRequest = new CancelOrderRequest();

                cancelOrderRequest.setOrder_no(orderFoodResponse.getOrderNo());
                cancelOrderRequest.setCustomer_id(orderFoodResponse.getCustomerId());
                cancelOrderRequest.setStatus("cancelled");

                Call<CancelOrderResponse> cancelOrderResponseCall = service.cancelOrder(cancelOrderRequest);
                cancelOrderResponseCall.enqueue(new Callback<CancelOrderResponse>() {
                    @Override
                    public void onResponse(Call<CancelOrderResponse> call, Response<CancelOrderResponse> response) {
                        if  (response.isSuccessful() && response.code() == 200) {
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            orders.remove(position);
                            notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<CancelOrderResponse> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            });
            AlertDialog dialog = builder.create();
            // Display the alert dialog on interface
            dialog.show();
        });
    }

    private void cancelOrder(Integer orderNo, Integer customerId, Integer position) {

    }


    @Override
    public int getItemCount() {
        return orders.size();
    }


    class TrackOrderHolder extends RecyclerView.ViewHolder {
        TextView orderNo;
        TextView orderType;
        TextView orderAt;
        Button viewOrder;
        Button cancelOrder;



        public TrackOrderHolder(View itemView) {
            super(itemView);
            orderNo = itemView.findViewById(R.id.orderNo);
            orderType = itemView.findViewById(R.id.orderType);
            orderAt = itemView.findViewById(R.id.orderAt);
            viewOrder = itemView.findViewById(R.id.btnViewDetails);
            cancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}
