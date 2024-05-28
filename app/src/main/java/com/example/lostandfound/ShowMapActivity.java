package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private LostFoundDatabaseHelper databaseHelper;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        mapView = findViewById(R.id.mapView);
        databaseHelper = new LostFoundDatabaseHelper(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        showAdvertsOnMap();
    }

    private void showAdvertsOnMap() {
        List<String> adverts = databaseHelper.getAllAdverts();
        Log.d("ShowMapActivity", "Number of adverts: " + adverts.size()); // Log the number of adverts retrieved
        for (String advert : adverts) {
            Log.d("ShowMapActivity", "Advert: " + advert); // Log each advert
            String[] parts = advert.split(",", 7); // Split into 7 parts, treating the remainder as the address

            if (parts.length == 7) {
                String location = parts[6];
                Log.d("ShowMapActivity", "Location: " + location); // Log each location
                LatLng latLng = getLocationFromAddress(location);
                if (latLng != null) {
                    Log.d("ShowMapActivity", "LatLng: " + latLng.toString()); // Log the LatLng
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(parts[2])); // Using name as title
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10)); // Adjust zoom level as needed
                } else {
                    Log.d("ShowMapActivity", "Geocoding failed for location: " + location);
                }
            } else {
                Log.d("ShowMapActivity", "Invalid advert format: " + advert);
            }
        }
    }



    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(strAddress, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
}


