package com.example.gpsappa;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationRequestCompat;

import com.example.gpsappa.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.security.Provider;
import java.util.List;

public class GPSService extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final Context context;
    private static final long updateDist = 1L; //updates every 1 m
    private static final long updateTime = 5000L; //updates every 1s
    protected LocationManager locationManager;
    private Location location;
    ActivityMainBinding binding;
    public static Location lastLocation;
    public static double lastDistance;
    public static int checkd;
    private FusedLocationProviderApi fuse;




    public GPSService(Context context, int type, ActivityMainBinding binding) {
        this.context = context;
        this.location = getCurrLocation();
        this.binding = binding;
        lastDistance = 0;
        lastLocation = null;
        checkd = 0;
        LocationRequestCompat request = new LocationRequestCompat.Builder(500).build();
        GoogleApiClient client = new GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        }, null);
    }

    private Location getCurrLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) {
            Log.v("TAG", "Provider is null");
            return null;
        } else {
            Log.v("TAG", "Provider: " + provider);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500L, 0f, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 1f, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 500L, 0f, this);
        return null;
    }

    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/
    private void turnGpsOn (Context context) {
        String beforeEnable = Settings.Secure.getString (context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String newSet = String.format ("%s,%s",
                beforeEnable,
                LocationManager.GPS_PROVIDER);
        try {
            Settings.Secure.putString (context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
                    newSet);
        } catch(Exception e) {e.printStackTrace();}
    }
    public void closeGPS(){
        if(locationManager != null)
            locationManager.removeUpdates(GPSService.this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //TODO: Display lat and long in layout from here
        System.out.println("listener called" + location.getLongitude());
        this.location = location;
        if(lastLocation == null) {
            binding.distance.setText("Distance: 0m");
        }else{
            if(checkd > 3) {
                if (!(location.distanceTo(lastLocation) >= 800))
                    lastDistance += location.distanceTo(lastLocation);
            }else
                checkd++;
            System.out.println("DistL " + location.distanceTo(lastLocation));
            binding.distance.setText("Distance: " + lastDistance);
        }
        lastLocation = location;
        try {
            binding.address.setText("Address: " + new Geocoder(context).getFromLocation(location.getLatitude(), location.getLongitude(),1).get(0).getAddressLine(0).trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.lat.setText("Latitude: " + location.getLatitude());
        binding.lon.setText("Longitude: " + location.getLongitude());
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
