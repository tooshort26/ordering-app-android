package com.e.maiplaceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.e.maiplaceapp.API.IUser;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.CustomerRequest;
import com.e.maiplaceapp.Models.CustomerResponse;
import com.e.maiplaceapp.Services.Service;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    AwesomeValidation mAwesomeValidation;
    private EditText email;
    private EditText password;
    private EditText firstName;
    private EditText middleName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText address;
    private Button btnRegister;

    Geocoder geocoder;
    List<Address> addressList;


    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

         email = findViewById(R.id.email);
         password = findViewById(R.id.password);
         firstName = findViewById(R.id.firstName);
         middleName = findViewById(R.id.middleName);
         lastName = findViewById(R.id.lastName);
         phoneNumber = findViewById(R.id.phoneNumber);
         address = findViewById(R.id.address);
         btnRegister = findViewById(R.id.signInWithMobile);



        // Initialize Form Validator
        mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.setColor(Color.YELLOW);  // optional, default color is RED if not set
        mAwesomeValidation.addValidation(this, R.id.firstName, "[A-Za-z ñ]+", R.string.err_firstname);
        mAwesomeValidation.addValidation(this, R.id.middleName, "[A-Za-z ñ]+", R.string.err_middlename);
        mAwesomeValidation.addValidation(this, R.id.lastName, "[A-Za-z ñ]+", R.string.err_lastname);
        mAwesomeValidation.addValidation(this, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        mAwesomeValidation.addValidation(this, R.id.phoneNumber, "(09|\\+639)\\d{9}", R.string.err_phone_number);

//        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
//        mAwesomeValidation.addValidation(this, R.id.password, regexPassword, R.string.err_password);



        geocoder = new Geocoder(this, Locale.getDefault());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        btnRegister.setOnClickListener(v -> {
            if  (mAwesomeValidation.validate()) {
                // Register user then proceed to dashboard
                this.registerUser();
                // Save it's token
            }
        });
    }


    private void forDevelopmentPurpose() {
        password.setText("oop");
        firstName.setText("christopher");
        lastName.setText("vistal");
        email.setText("christophervistal26@gmail.com");
        phoneNumber.setText("09193693499");
        address.setText("Tandag City");
    }

    private void registerUser() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Retrofit retrofit = Service.RetrofitInstance(this);
        IUser service    = retrofit.create(IUser.class);

        Call<CustomerResponse> customerResponseCall = service.register(
                new CustomerRequest(
                         password.getText().toString(),
                        firstName.getText().toString(), middleName.getText().toString(),
                        lastName.getText().toString(), email.getText().toString(),
                        phoneNumber.getText().toString(), address.getText().toString()
                )
        );

        customerResponseCall.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                if  (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"is_logged", true);
                    SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", response.body().getId());
                    Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {

                               /* Log.d("LOCATION_LATITUDE", String.valueOf(location.getLatitude()));
                                Log.d("LOCATION_LONGITUDE", String.valueOf(location.getLongitude()));*/
                               try {
                                   addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                   String address = addressList.get(0).getAddressLine(0);
                                   String area = addressList.get(0).getLocality();
                                   String city = addressList.get(0).getAdminArea();
                                   String country = addressList.get(0).getCountryName();
                                   Log.d("ADDRESS_OF_USER", address + area + city + country);
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d("LOCATION_LATITUDE", String.valueOf(mLastLocation.getLatitude()));
            Log.d("LOCATION_LONGITUDE", String.valueOf(mLastLocation.getLongitude()));
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }


}
