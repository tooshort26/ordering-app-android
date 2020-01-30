package com.e.maiplaceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.maiplaceapp.API.IUser;
import com.e.maiplaceapp.Dialogs.EnterPhoneNumberDialog;
import com.e.maiplaceapp.Dialogs.VerifyCodeDialog;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.CustomerLoginRequest;
import com.e.maiplaceapp.Models.CustomerLoginResponse;
import com.e.maiplaceapp.Models.CustomerRequest;
import com.e.maiplaceapp.Models.CustomerResponse;
import com.e.maiplaceapp.Services.Service;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
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
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener , VerifyCodeDialog.sendInputtedCodeListener, EnterPhoneNumberDialog.sendInputPhoneNumber {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 3000;

    private ProgressDialog progressDialog;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView email;
    private TextView password;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressDialog phoneProgressDialog;


    private String verificationId;
    private String phoneNumber;



 /*   private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.2:3030");
        } catch (URISyntaxException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        Toast.makeText(this, String.valueOf(data), Toast.LENGTH_LONG).show();
    });
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.userAlreadyLoggedIn();

        setContentView(R.layout.activity_login);
        findViewById(R.id.activityLogin).requestFocus();


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        setGooglePlusButtonText(signInButton, "Sign in with google");

        findViewById(R.id.sign_in_button).setOnClickListener(this);

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


        this.isUserLoggedInByFacebook();

        this.normalSignIn();




        findViewById(R.id.signInWithMobile).setOnClickListener(v -> {

            EnterPhoneNumberDialog enterPhoneNumberDialog = new EnterPhoneNumberDialog();
            enterPhoneNumberDialog.show(getSupportFragmentManager(), "enter_phone_number_dialog");

        });


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);



        logoutAnySocialMedia();

//        mSocket.connect();
//        mSocket.on("customer_notify_", onNewMessage);

//        findViewById(R.id.sendData).setOnClickListener(v -> mSocket.emit("testing", "This is a sample text"));

    }

    @Override
    public void applyPhoneNumber(String phone_number) {

        phoneProgressDialog = new ProgressDialog(this);
        phoneProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        phoneProgressDialog.setCancelable(false);
        phoneProgressDialog.setMessage("Please wait...");
        phoneProgressDialog.show();
        phoneNumber = phone_number;


//        Toast.makeText(this, phone_number, Toast.LENGTH_SHORT).show();
//        String phone = "+639504156122";

         PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);

    }


    @Override
    public void applyTexts(String inputtedCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, inputtedCode);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String sendCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(sendCode, forceResendingToken);
            phoneProgressDialog.dismiss();
            VerifyCodeDialog verifyCodeDialog = new VerifyCodeDialog();
            verifyCodeDialog.show(getSupportFragmentManager(), "verify_code_dialog");
            verificationId = sendCode;
            SharedPref.setSharedPreferenceString(getApplicationContext(),"registered_phone_number", phoneNumber);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };



    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
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


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {


                        Retrofit retrofit = Service.RetrofitInstance(getApplicationContext());
                        IUser service    = retrofit.create(IUser.class);


                        Call<CustomerResponse> customerResponseCall = service.register(
                                new CustomerRequest(
                                        null,
                                        acct.getGivenName(), acct.getGivenName(),
                                        acct.getFamilyName(), acct.getEmail(),
                                        null, null
                                )
                        );

                        customerResponseCall.enqueue(new Callback<CustomerResponse>() {
                            @Override
                            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                                if  (response.isSuccessful()) {
                                    SharedPref.setSharedPreferenceString(getApplicationContext(),"firstname", acct.getGivenName());
                                    SharedPref.setSharedPreferenceString(getApplicationContext(),"lastname", acct.getFamilyName());

                                    SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"is_logged", true);
                                    SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"logout_social_media", true);
                                    SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"from_third_party", true);
                                    SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", response.body().getId());
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                                progressDialog.dismiss();
//                                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.activityLogin), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }

                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }




    private void normalSignIn() {
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
                        SharedPref.setSharedPreferenceString(getApplicationContext(),"firstname", response.body().getFirstname());
                        SharedPref.setSharedPreferenceString(getApplicationContext(),"lastname", response.body().getLastname());

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
    }


    private void logoutAnySocialMedia() {
        if(SharedPref.getSharedPreferenceBoolean(this, "logout_social_media", false)) {
            LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Toast.makeText(this, "There is a user already logged in", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void isUserLoggedInByFacebook() {
        Profile profile = Profile.getCurrentProfile();
        if(profile != null) {
//            Toast.makeText(this, "The user logged in by facebook", Toast.LENGTH_SHORT).show();
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
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, (object, response) -> {
            try {
                String first_name = object.getString("first_name");
                String last_name = object.getString("last_name");
                String userId = object.getString("id");
                String image_url = "https://graph.facebook.com/" + userId + "picture?type=normal";

                SharedPref.setSharedPreferenceString(getApplicationContext(),"firstname", first_name);
                SharedPref.setSharedPreferenceString(getApplicationContext(),"lastname", last_name);
                SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"from_facebook_login", true);
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    @Override
    public void onClick(View v) {
        // For google sign in.
        switch(v.getId()) {
            case  R.id.sign_in_button :
                signIn();
                break;
        }
    }



}
