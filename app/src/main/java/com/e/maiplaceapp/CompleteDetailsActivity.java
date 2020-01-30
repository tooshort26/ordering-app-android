package com.e.maiplaceapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.e.maiplaceapp.API.IUser;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileRequest;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileResponse;
import com.e.maiplaceapp.Services.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class CompleteDetailsActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_details);
        EditText phoneNumber = findViewById(R.id.editTextPhoneNumber);
        EditText address = findViewById(R.id.editTextAddress);
        EditText email = findViewById(R.id.editTextEmail);

        // Initialize Form Validator
        mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.setColor(Color.YELLOW);  // optional, default color is RED if not set
        mAwesomeValidation.addValidation(this, R.id.editTextEmail, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        mAwesomeValidation.addValidation(this, R.id.editTextPhoneNumber, "(09|\\+639)\\d{9}", R.string.err_phone_number);



        int customerId =  getIntent().getIntExtra("customer_id",0);

        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
             if(mAwesomeValidation.validate() && !address.getText().toString().isEmpty()) {
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
                            SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"logout_social_media", true);
                            SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", response.body().getId());
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<CustomerUpdateProfileResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(CompleteDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                 if(address.getText().toString().isEmpty() || address.getText().toString().trim().length() <= 0) {
                     address.setError("Please check your address");
                 }
             }

        });
    }

    @Override
    public void onBackPressed() {
    // Remove the super call method so the user can't back to the previous intent if not filling all fields.
    }
}
