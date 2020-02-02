package com.e.maiplaceapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StoreLocatorActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_locator);


        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            case ConnectionResult.SUCCESS:
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(this, "Google Play Services Missing", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                //The user has to update play services
                Toast.makeText(this, "Update Google Play Services", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng MaiPlace = new LatLng(8.185727, 126.354835);
        map.addMarker(new MarkerOptions().position(MaiPlace).title("Mai Place"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(MaiPlace));
        float zoomLevel = 18.0f; //This goes up to 21
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MaiPlace, zoomLevel));
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
