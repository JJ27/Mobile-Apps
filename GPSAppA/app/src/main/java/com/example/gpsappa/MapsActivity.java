package com.example.gpsappa;

import androidx.fragment.app.FragmentActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gpsappa.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapListener.CombinedListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    List<Location> waypoints;
    List<String> waypointNames;
    static String polyline;
    static LatLng boundsw, boundne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GPSApplication app = (GPSApplication) getApplicationContext();
        waypoints = app.getWaypoints();
        waypointNames = app.getWayPointNames();
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
        LatLng lastloc = new LatLng(-34, 121);
        for(int i = 0; i < waypoints.size(); i++){
            Location loc = waypoints.get(i);
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(waypointNames.get(i)));
            lastloc = latLng;
        }
        if(polyline != null) {
            drawRoutes(polyline);
            moveCamera();
        } else{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastloc, 8f));
        }
    }

    private void moveCamera() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(boundsw)
                        .include(boundne)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            }
        });
    }

    private void drawRoutes(String s){
        s = s.replaceAll("\"","");
        mMap.addPolyline(new PolylineOptions()
            .color(R.color.black)
            .width(10f)
            .clickable(false)
            .addAll(MapUtils.readPolyline(s))
        );
    }
    public static void setPolyline(String polyline) {
        MapsActivity.polyline = polyline;
    }
    public static void setBounds(LatLng boundsw, LatLng boundne) {
        MapsActivity.boundsw = boundsw;
        MapsActivity.boundne = boundne;
    }
    public class APIReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            polyline = intent.getStringExtra("polyline");
        }
    }
}