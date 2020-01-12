package com.e.maiplaceapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.e.maiplaceapp.API.IOrder;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.FoodResponse;
import com.e.maiplaceapp.Models.Receipt.CustomerReceiptResponse;
import com.e.maiplaceapp.Models.Receipt.Food;
import com.e.maiplaceapp.Models.Receipt.OrderFood;
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
public class ReceiptFragment extends Fragment {

    private TextView txtOrderNo;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtPhone;
    private TextView txtOrderDate;
    private TextView txtSubTotal;
    private TextView txtTotal;
    private TableLayout tableLayout;


    int subTotal = 0;
    int total = 0;


    private static final String TAG = "ReceiptFragment";
    private List<FoodResponse> customerCartResponseList = new ArrayList<>();
    ProgressDialog progressDialog;

    public ReceiptFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtOrderNo = view.findViewById(R.id.orderNo);
        txtName = view.findViewById(R.id.customerName);
        txtAddress = view.findViewById(R.id.customerAddress);
        txtPhone = view.findViewById(R.id.customerPhone);
        txtOrderDate = view.findViewById(R.id.customerOrderDate);
        txtSubTotal = view.findViewById(R.id.subTotal);
        txtTotal = view.findViewById(R.id.total);
        txtOrderDate = view.findViewById(R.id.customerOrderDate);
        tableLayout = view.findViewById(R.id.tableview);


        int order_no = Integer.parseInt(getArguments().getString("order_no"));
        this.requestReceiptDetails(order_no);



    }

    private void requestReceiptDetails(int order_no) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        txtOrderNo.setText(String.format("Order #: %d", order_no));
        int customer_id = SharedPref.getSharedPreferenceInt(getContext(), "customer_id", 0);
        Retrofit retrofit = Service.RetrofitInstance(getContext());
        IOrder service    = retrofit.create(IOrder.class);

        Call<CustomerReceiptResponse>  customerReceiptResponseCall = service.getReceipt(customer_id, order_no);
        customerReceiptResponseCall.enqueue(new Callback<CustomerReceiptResponse>() {
            @Override
            public void onResponse(Call<CustomerReceiptResponse> call, Response<CustomerReceiptResponse> response) {
                if  (response.isSuccessful()) {
                    CustomerReceiptResponse receiptResponse = response.body();

                    txtName.setText(String.format("%s %s", receiptResponse.getCustomer().getFirstname(), receiptResponse.getCustomer().getLastname()));
                    txtAddress.setText(receiptResponse.getCustomer().getAddress());
                    txtPhone.setText(receiptResponse.getCustomer().getPhoneNumber());
                    txtOrderDate.setText(receiptResponse.getCreatedAt());


                    int i = 0;
                    for(Food order : receiptResponse.getFoods()) {
                        i++;
                        TableRow row = new TableRow(getContext());
                        for(OrderFood orderFood : order.getOrderFood()) {

                            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                            row.setLayoutParams(lp);

                            TextView itemTextView = new TextView(getContext());
                            TextView quantityTextView = new TextView(getContext());
                            TextView priceTextView = new TextView(getContext());
                            TextView totalTextView = new TextView(getContext());

                            itemTextView.setText(orderFood.getName());
                            itemTextView.setGravity(Gravity.CENTER);
                            quantityTextView.setText(String.valueOf(order.getQuantity()));
                            quantityTextView.setGravity(Gravity.CENTER);
                            priceTextView.setText("P" + orderFood.getPrice());
                            priceTextView.setGravity(Gravity.LEFT);
                            totalTextView.setText("P" + (orderFood.getPrice() * order.getQuantity()));
                            totalTextView.setGravity(Gravity.LEFT);

                            subTotal += orderFood.getPrice() * order.getQuantity();
                            total += orderFood.getPrice() * order.getQuantity();

                            row.addView(itemTextView);
                            row.addView(quantityTextView);
                            row.addView(priceTextView);
                            row.addView(totalTextView);
                        }
                        tableLayout.addView(row, i);

                    }

                    txtSubTotal.setText("P" + subTotal);
                    txtTotal.setText("P" + total);



                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<CustomerReceiptResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });



    }
}
