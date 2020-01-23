package com.e.maiplaceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.maiplaceapp.API.ICategory;
import com.e.maiplaceapp.API.IUser;
import com.e.maiplaceapp.Adapters.CategoryAdapter;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Helpers.Strings;
import com.e.maiplaceapp.Models.Category.CategoryResponse;
import com.e.maiplaceapp.Models.CustomerRequest;
import com.e.maiplaceapp.Models.CustomerResponse;
import com.e.maiplaceapp.Services.Service;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DashboardActivity extends AppCompatActivity {

    private Socket mSocket;
   {
       try {
           mSocket = IO.socket("http://192.168.1.2:3030");
       } catch (URISyntaxException e) {
           Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
       }
   }

   private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
       JSONObject data = (JSONObject) args[0];
       try {
           displayNotification( data.getString("text"));
       } catch (JSONException e) {
           e.printStackTrace();
       }
   });


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout frameLayout;
    private LinearLayout dashboardMainLayout;
    private LinearLayout mainLayout;
    NavigationView navigationView;

    private Button btnSignout, btnViewMenu;

    private ProgressDialog progressDialog;


    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;


    List<CategoryResponse> categories = new ArrayList<>();

    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String eventName = "customer_notify_" + SharedPref.getSharedPreferenceInt(this,"customer_id",0);
        mSocket.connect();
        mSocket.on(eventName, onNewMessage);

        notificationManager = NotificationManagerCompat.from(this);


        userFromFacebookRegister();


        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);

        // FrameLayout for fragments
        frameLayout = findViewById(R.id.flContent);

        // Layout for main
        mainLayout = findViewById(R.id.mainLayout);

        // Buttons
/*        btnViewMenu = findViewById(R.id.btnViewMenu);
        btnSignout = findViewById(R.id.btnSignOut);*/

        // Dashboard main layout
        dashboardMainLayout = findViewById(R.id.dashboardMainLayout);

        // Set the focus on the layout on the Search Field.
        dashboardMainLayout.requestFocus();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nvDrawer = findViewById(R.id.nvView);


        // Set the header name of navigation to the current user.
        navigationView = findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);


      /*  btnSignout.setOnClickListener(v -> {
            SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"is_logged", false);
            SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", 0);
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });*/

        /*btnViewMenu.setOnClickListener(v -> {
            dashboardMainLayout.removeView(mainLayout);
            frameLayout.setVisibility(View.VISIBLE);
            FoodFragment foodFragment = new FoodFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, foodFragment, "FoodFragment").commit();
        });*/


       this.displayUserNameAndSetEventInNavigation();

        this.requestCategories();

    }


    private void displayNotification(String message) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.maiplace)
                    .setContentTitle("Mai Place")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManager.notify(1, notification);
        } else {
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("Mai Place")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.maiplace)
                        .build();
                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(2, notification);
        }

    }


    public void getSample(int id)
    {
        dashboardMainLayout.removeView(mainLayout);
        frameLayout.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putInt("category_id", id);
        FoodFragment foodFragment = new FoodFragment();
        foodFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, foodFragment, "FoodFragment").commit();
    }


    public void displayUserNameAndSetEventInNavigation() {
        View hView =  navigationView.getHeaderView(0);
        TextView navUsername = hView.findViewById(R.id.userName);
        ImageView navImage = hView.findViewById(R.id.userProfile);
        String customerName = Strings.capitalize(SharedPref.getSharedPreferenceString(this,"firstname", "")) + " " + Strings.capitalize(SharedPref.getSharedPreferenceString(this,"lastname", ""));
        navUsername.setText(customerName);
        navUsername.setOnClickListener(v -> this.gotoUserProfile() );
        navImage.setOnClickListener(v -> this.gotoUserProfile());
    }

    private void gotoUserProfile()
    {
        Fragment fragment = null;
        Class fragmentClass = ProfileFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardMainLayout.removeView(mainLayout);
        frameLayout.setVisibility(View.VISIBLE);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        dashboardMainLayout.removeView(mainLayout);
        frameLayout.setVisibility(View.VISIBLE);

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        mDrawer.closeDrawers();


    }



    private void userFromFacebookRegister() {
        if(SharedPref.getSharedPreferenceBoolean(this,"from_facebook_login", false)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            String first_name = SharedPref.getSharedPreferenceString(this,"firstname", null);
            String last_name = SharedPref.getSharedPreferenceString(this,"lastname", null);

            Retrofit retrofit = Service.RetrofitInstance(this);
            IUser service    = retrofit.create(IUser.class);

            Call<CustomerResponse> customerResponseCall = service.register(
                    new CustomerRequest(
                            null,
                            first_name, null,
                            last_name, null,
                            null, null
                    )
            );

            customerResponseCall.enqueue(new Callback<CustomerResponse>() {
                @Override
                public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                    if  (response.isSuccessful()) {
                        progressDialog.dismiss();
                        SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"is_logged", true);
                        SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"from_third_party", true);
                        SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", response.body().getId());
                        SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"from_facebook_login", false);
                        Intent intent = new Intent(DashboardActivity.this, CompleteDetailsActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<CustomerResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //TODO Save the categories first in DB then perform search.
   /* private void filter(String text) {
    ArrayList<CategoryResponse> filteredList = new ArrayList<>();

        for (CategoryResponse item : categories) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        categoryAdapter.filterList(filteredList);
    }
*/

   private void requestCategories() {


       progressDialog = new ProgressDialog(this);
       progressDialog.setMessage("Please wait...");
       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       progressDialog.setCancelable(false);
       progressDialog.show();

       Retrofit retrofit     = Service.RetrofitInstance(getApplicationContext());
        ICategory service    = retrofit.create(ICategory.class);

        Call<List<CategoryResponse>> categoryResponseCall = service.get();

        categoryResponseCall.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                categories = response.body();

                categoryAdapter = new CategoryAdapter(categories, getApplicationContext());
                recyclerView = findViewById(R.id.category_recycler_view);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                recyclerView.setAdapter(categoryAdapter);
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = CartFragment.class;
        boolean isFragment = false;
        String title = "Mai Place";

        switch(menuItem.getItemId()) {
            case R.id.cart:
                fragmentClass = CartFragment.class;
                isFragment = true;
                title = "Your cart";
                break;

            case R.id.menu:
                Intent intent1 = new Intent(this, DashboardActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;

            case R.id.track_orders:
                fragmentClass = TrackOrderFragment.class;
                isFragment = true;
                title = "Track your orders";
                break;

            case R.id.account_setting:
                fragmentClass = ProfileFragment.class;
                isFragment = true;
                title = "Your profile";
                break;

            case R.id.store_locator:
                Intent storeLocatorIntent = new Intent(this, StoreLocatorActivity.class);
                storeLocatorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(storeLocatorIntent);
                break;

            case R.id.sign_out:

                SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"is_logged", false);
                SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", 0);
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }

        if  (isFragment) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dashboardMainLayout.removeView(mainLayout);
            frameLayout.setVisibility(View.VISIBLE);
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }



        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(title);
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
