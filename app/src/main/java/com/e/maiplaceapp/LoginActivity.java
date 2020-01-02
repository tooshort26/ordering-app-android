package com.e.maiplaceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.maiplaceapp.API.IUser;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.CustomerLoginRequest;
import com.e.maiplaceapp.Models.CustomerLoginResponse;
import com.e.maiplaceapp.Services.Service;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progressDialog;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    // This code is for google sign in.
    private static final int RC_SIGN_IN = 9001;



//    private Socket mSocket;
//    {
//        try {
//            mSocket = IO.socket("http://192.168.1.4:3030");
//        } catch (URISyntaxException e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
//        JSONObject data = (JSONObject) args[0];
//        Toast.makeText(this, String.valueOf(data), Toast.LENGTH_LONG).show();
//    });

    private TextView email;
    private TextView password;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userAlreadyLoggedIn();
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            // User not sign in
            Toast.makeText(this,"User not sign in using google.", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        this.setGoogleSignButtonText(signInButton, "Login with google");




        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        this.forDevelopment();

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            Retrofit retrofit = Service.RetrofitInstance(this);
            IUser service = retrofit.create(IUser.class);

            Call<CustomerLoginResponse> customerLoginResponseCall = service.login(new CustomerLoginRequest(email.getText().toString(), password.getText().toString()));

            customerLoginResponseCall.enqueue(new Callback<CustomerLoginResponse>() {
                @Override
                public void onResponse(Call<CustomerLoginResponse> call, Response<CustomerLoginResponse> response) {
                    if(response.isSuccessful() && response.body().getCode() == 200) {
                        progressDialog.dismiss();
                        SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"is_logged", true);
                        SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", response.body().getId());

                        proceedToDashboard();
                    } else {
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<CustomerLoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });



        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });


//        mSocket.connect();
//        mSocket.on("sample", onNewMessage);

//        findViewById(R.id.sendData).setOnClickListener(v -> mSocket.emit("testing", "This is a sample text"));

    }

    protected void setGoogleSignButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Update the UI
            Log.d("FROM_GOOGLE_SIGN_IN", account.getDisplayName() + " ," + account.getEmail() + ","  + account.getPhotoUrl());

        } catch (ApiException e) {
            e.printStackTrace();
            Log.d("FROM_GOOGLE_SIGN_IN", "Sign in result:failed code = " + e.getStatusCode());
            // Update the ui with null values.
        }

    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if  (currentAccessToken != null) {
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "picture?type=normal";

                    Log.d("ACCOUNT_INFORMATION", first_name + "," + last_name + "," + email + "," +  id + "," + image_url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void userAlreadyLoggedIn() {
        if (SharedPref.getSharedPreferenceBoolean(getApplicationContext(),"is_logged", false)) {
            proceedToDashboard();
        }
    }

    private void proceedToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void forDevelopment() {
        email.setText("christophervistal26@gmail.com");
        password.setText("oop");
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> Toast.makeText(LoginActivity.this, "Google Sign out", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        // For google sign in.
        switch(v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.sign_out_button:
                signOut();
                break;
        }
    }


}
