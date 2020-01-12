package com.e.maiplaceapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.e.maiplaceapp.API.IUser;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileRequest;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileResponse;
import com.e.maiplaceapp.Services.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CompleteDetailsActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_details);
        EditText phoneNumber = findViewById(R.id.editTextPhoneNumber);
        EditText address = findViewById(R.id.editTextAddress);
        EditText email = findViewById(R.id.editTextEmail);

        int customerId = SharedPref.getSharedPreferenceInt(this,"customer_id", 0);


        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            if(customerId != 0) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                Retrofit retrofit = Service.RetrofitInstance(getApplicationContext());
                IUser service    = retrofit.create(IUser.class);

                CustomerUpdateProfileRequest customerUpdateProfileRequest = new CustomerUpdateProfileRequest();
                customerUpdateProfileRequest.setEmail(email.getText().toString());
                customerUpdateProfileRequest.setAddress(address.getText().toString());
                customerUpdateProfileRequest.setPhone_number(phoneNumber.getText().toString());
                customerUpdateProfileRequest.setId(customerId);
                Call<CustomerUpdateProfileResponse> customerUpdateProfileResponseCall = service.update(customerUpdateProfileRequest);


                customerUpdateProfileResponseCall.enqueue(new Callback<CustomerUpdateProfileResponse>() {
                    @Override
                    public void onResponse(Call<CustomerUpdateProfileResponse> call, Response<CustomerUpdateProfileResponse> response) {
                        if(response.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(CompleteDetailsActivity.this, "Successfully update your profile.", Toast.LENGTH_SHORT).show();
                            SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"from_third_party", false);
                            SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"from_facebook_login", false);
                            SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"logout_social_media", true);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<CustomerUpdateProfileResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(CompleteDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
}
