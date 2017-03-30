package com.jets.mashaweer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jets.classes.RootParser;
import com.jets.classes.Trip;
import com.jets.classes.VolleySingleton;
import com.jets.fragments.NavBarFragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HistoryActivity extends AppCompatActivity implements OnMapReadyCallback {


    MapView mapView;
    SupportMapFragment mapFragment;
    VolleySingleton singleton;
    RequestQueue requestQueue;
    RootParser rootParser;
    ArrayList<Trip> trips;
    Double latitudeSource, longitudeSource, latitudeDest, longitudeDest;
    PolylineOptions lineOptions;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        NavBarFragment fragment = (NavBarFragment) getSupportFragmentManager().findFragmentById(R.id.navbar);
        fragment.setBtnColor("map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        singleton = VolleySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();
        rootParser = new RootParser();

        Intent intent = getIntent();
        trips = (ArrayList<Trip>) intent.getSerializableExtra("pastTrips");

        mapFragment.getMapAsync(this);
        mapFragment.onResume();
    }

    ///////////// ramadaaaan
    ////// 1.   AIzaSyDzKDkgQVKv6HrxWxk5Prg2fsfN11igaDQ  >>> my Account
    ///// 2.    AIzaSyCTJY-IKKDKRNA_YSOemPS5EqkbTX7NM_g  >>>> mashaweer account

    @Override
    protected void onResume() {
        super.onResume();

        for (Trip trip : trips) {
            Log.i("history trip", trip.getTripStartLongLat() + "&destination=" + trip.getTripEndLongLat());
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + trip.getTripStartLongLat() + "&destination=" + trip.getTripEndLongLat() + "&key=AIzaSyCTJY-IKKDKRNA_YSOemPS5EqkbTX7NM_g";
            draw(url);
        }
    }

    public void draw(String url) {
        final ArrayList<LatLng> points= new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("MyTag"," ------------------- success"+response);


                try {
                    String status = response.getString("status");

                    Log.i("MyTag",">>> History Result Status >>> " + status);

                    if ( status.equalsIgnoreCase("ZERO_RESULTS") )
                    {
//                        Toast.makeText(getApplicationContext(), "OOPS! ... Network Error Occurred!", Toast.LENGTH_SHORT).show();

                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<List<HashMap<String, String>>> roots = rootParser.parse(response);

                for (int i = 0; i < roots.size(); i++) {

                    List<HashMap<String, String>> path = roots.get(i);

                    for (int j = 0; j < path.size(); j++) {

                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        if (j == 0) {
                            latitudeSource = lat;
                            longitudeSource = lng;
                        }
                        if (j == path.size() - 1) {
                            latitudeDest = lat;
                            longitudeDest = lng;
                        }
                        points.add(position);
                    }
                }
                mMap.addPolyline(new PolylineOptions().addAll(points).width(8).color(new Random().nextInt()+100));
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeSource, longitudeSource)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeDest, longitudeDest)));

//                Toast.makeText(getApplicationContext(), "Preparing Your Map History", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });
        singleton.addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

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

        mMap=googleMap;
        System.out.println("ana ready fel map");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(26.8206, 30.8025), 5));



//        float zoomLevel = 16.0f; //This goes up to 21
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.0128, 31.1586), zoomLevel));
//
//        mMap.addMarker(new MarkerOptions().position(new LatLng(30.0128, 31.1586)).title("Trip One"));
//        PolylineOptions polylineOptions = new PolylineOptions();
//        polylineOptions.add(new LatLng(30.0128, 31.1586));
//        polylineOptions.add(new LatLng(30.0129, 31.1588));
//        polylineOptions.width(10);
//        polylineOptions.color(Color.RED);
//        mMap.addPolyline(polylineOptions);
//        PolylineOptions polylineOptions2 = new PolylineOptions();
//        polylineOptions2.add(new LatLng(30.0128, 31.1586));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(30.0128, 31.1586)).title("Trip Two"));
//        polylineOptions2.add(new LatLng(30.0129, 29.9));
//        polylineOptions2.width(10);
//        polylineOptions2.color(Color.BLUE);
//        mMap.addPolyline(polylineOptions2);
    }
}
