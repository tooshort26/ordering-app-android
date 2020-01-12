package com.e.maiplaceapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.e.maiplaceapp.API.IUser;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.Profile.CustomerProfileResponse;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileRequest;
import com.e.maiplaceapp.Models.ThirdPartyLogin.CustomerUpdateProfileResponse;
import com.e.maiplaceapp.Services.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText email = view.findViewById(R.id.email);
        EditText firstName = view.findViewById(R.id.firstName);
        EditText lastName = view.findViewById(R.id.lastName);
        EditText address = view.findViewById(R.id.address);
        EditText phoneNumber = view.findViewById(R.id.phoneNumber);

        Retrofit retrofit = Service.RetrofitInstance(getContext());
        IUser service    = retrofit.create(IUser.class);

        int customer_id = SharedPref.getSharedPreferenceInt(getContext(),"customer_id", 0);
        if(customer_id != 0) {
            Call<CustomerProfileResponse> customerProfileResponseCall = service.getProfile(customer_id);
            customerProfileResponseCall.enqueue(new Callback<CustomerProfileResponse>() {
                @Override
                public void onResponse(Call<CustomerProfileResponse> call, Response<CustomerProfileResponse> response) {
                    CustomerProfileResponse profileResponse = response.body();
                    email.setText(profileResponse.getEmail());
                    firstName.setText(profileResponse.getFirstname());
                    lastName.setText(profileResponse.getLastname());
                    address.setText(profileResponse.getAddress());
                    phoneNumber.setText(profileResponse.getPhoneNumber());
                }

                @Override
                public void onFailure(Call<CustomerProfileResponse> call, Throwable t) {

                }
            });
        } else {
            Toast.makeText(getContext(), "There is a problem try to visit this section again.", Toast.LENGTH_SHORT).show();
        }

        // Update the profile of user.
        view.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            Retrofit retrofit1 = Service.RetrofitInstance(getContext());
            IUser service1 = retrofit1.create(IUser.class);

            CustomerUpdateProfileRequest customerUpdateProfileRequest = new CustomerUpdateProfileRequest();
            customerUpdateProfileRequest.setFirstname(firstName.getText().toString());
            customerUpdateProfileRequest.setLastname(lastName.getText().toString());
            customerUpdateProfileRequest.setEmail(email.getText().toString());
            customerUpdateProfileRequest.setAddress(address.getText().toString());
            customerUpdateProfileRequest.setPhone_number(phoneNumber.getText().toString());
            customerUpdateProfileRequest.setId(customer_id);
            Call<CustomerUpdateProfileResponse> customerUpdateProfileResponseCall = service1.update(customerUpdateProfileRequest);


            customerUpdateProfileResponseCall.enqueue(new Callback<CustomerUpdateProfileResponse>() {
                @Override
                public void onResponse(Call<CustomerUpdateProfileResponse> call, Response<CustomerUpdateProfileResponse> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(getContext(), "Successfully update your profile.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CustomerUpdateProfileResponse> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
