package com.e.maiplaceapp;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng MaiPlace = new LatLng(8.185123, 126.353053);
        map.addMarker(new MarkerOptions().position(MaiPlace).title("Mai Place"));
        map.moveCamera(CameraUpdateFactory.newLatLng(MaiPlace));
    }
}
