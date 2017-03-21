package com.jets.activites;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.0128, 31.1586), zoomLevel));

        mMap.addMarker(new MarkerOptions().position(new LatLng(30.0128, 31.1586)).title("Trip One"));
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(new LatLng(30.0128, 31.1586));
        polylineOptions.add(new LatLng(30.0129, 31.1588));
        polylineOptions.width(10);
        polylineOptions.color(Color.RED);
        mMap.addPolyline(polylineOptions);
        PolylineOptions polylineOptions2 = new PolylineOptions();
        polylineOptions2.add(new LatLng(30.0128, 31.1586));
        mMap.addMarker(new MarkerOptions().position(new LatLng(30.0128, 31.1586)).title("Trip Two"));
        polylineOptions2.add(new LatLng(30.0129, 29.9));
        polylineOptions2.width(10);
        polylineOptions2.color(Color.BLUE);
        mMap.addPolyline(polylineOptions2);
    }
}
