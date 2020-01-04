package com.e.maiplaceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.e.maiplaceapp.Adapters.CategoryAdapter;
import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.Models.Category.CategoryResponse;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.navigation.NavigationView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout frameLayout;
    private LinearLayout dashboardMainLayout;
    private LinearLayout mainLayout;

    private Button btnSignout, btnViewMenu;

    private ProgressDialog progressDialog;


//    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    LinearLayoutManager layoutManager;

    List<CategoryResponse> categories = new ArrayList<>();

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.10:3030");
        } catch (URISyntaxException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

 /*   private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        Toast.makeText(this, String.valueOf(data), Toast.LENGTH_LONG).show();
    });

*/

//        findViewById(R.id.sendData).setOnClickListener(v -> mSocket.emit("testing", "This is a sample text"));




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mSocket.connect();



    /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.e.maiplaceapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        */



//        this.requestCategories();


        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);

        // FrameLayout for fragments
        frameLayout = findViewById(R.id.flContent);

        // Layout for main
        mainLayout = findViewById(R.id.mainLayout);

        // Buttons
        btnViewMenu = findViewById(R.id.btnViewMenu);
        btnSignout = findViewById(R.id.btnSignOut);

        // Dashboard main layout
        dashboardMainLayout = findViewById(R.id.dashboardMainLayout);

        // Set the focus on the layout on the Search Field.
        dashboardMainLayout.requestFocus();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nvDrawer = findViewById(R.id.nvView);

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


        btnSignout.setOnClickListener(v -> {
            SharedPref.setSharedPreferenceBoolean(getApplicationContext(),"is_logged", false);
            SharedPref.setSharedPreferenceInt(getApplicationContext(),"customer_id", 0);

            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnViewMenu.setOnClickListener(v -> {
            dashboardMainLayout.removeView(mainLayout);
            frameLayout.setVisibility(View.VISIBLE);
            FoodFragment foodFragment = new FoodFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, foodFragment, "FoodFragment").commit();
        });



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

  /*  private void requestCategories() {
        Retrofit retrofit     = Service.RetrofitInstance(getApplicationContext());
        ICategory service    = retrofit.create(ICategory.class);

        Call<List<CategoryResponse>> categoryResponseCall = service.get();

        categoryResponseCall.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if(response.isSuccessful() && response.code() == 200) {

                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }*/

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

        switch(menuItem.getItemId()) {
            case R.id.cart:
                fragmentClass = CartFragment.class;
                isFragment = true;
                break;

//            case R.id.nav_dashboard:
//                Intent intent1 = new Intent(this, DashboardActivity.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent1);
//                break;


            case R.id.menu:
                dashboardMainLayout.removeView(mainLayout);
                frameLayout.setVisibility(View.VISIBLE);
                FoodFragment foodFragment = new FoodFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, foodFragment, "FoodFragment").commit();
                break;




            default:
                fragmentClass = CartFragment.class;
                isFragment = true;
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
        setTitle(menuItem.getTitle());
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
