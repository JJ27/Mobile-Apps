package com.example.gpsappa;


import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationRequestCompat;

import com.example.gpsappa.databinding.ActivityMainBinding;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GPSService extends AppCompatActivity {
    private final Context context;
    protected LocationManager locationManager;
    ActivityMainBinding binding;
    private FusedLocationProviderClient fuse;
    LocationCallback callback;
    LocationRequest locationRequest;
    Address currentAddy;
    List<Address> prevAddy;
    Location currLoc;
    List<Location> prevLoc;
    float totaldist;


    public GPSService(Context context, int type, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
        this.totaldist = 0f;
        fuse = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Denied");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else{
            fuse.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUI(location);
                }
            });
        }
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                System.out.println("CALLBACK");
                updateUI(locationResult.getLastLocation());
            }
        };
        locationRequest = LocationRequest.create().setInterval(1000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 101:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fuse.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            updateUI(location);
                            currLoc = location;
                        }
                    });
                } else{
                    Toast.makeText(context, "This app requires location permissions!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateUI(Location loc){
        binding.lat.setText("Latitude: " + loc.getLatitude());
        binding.lon.setText("Longitude: " + loc.getLongitude());
        //TODO: Include speed using loc.hasSpeed() and loc.getSpeed()
        Geocoder gc = new Geocoder(context);
        Address s = null;
        try {
            s = gc.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1).get(0);
            binding.address.setText(s.getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GPSApplication app = (GPSApplication) context.getApplicationContext();
        prevLoc = app.getLocations();
        prevAddy = app.getAddresses();
        try {
            if(loc.distanceTo(prevLoc.get(prevLoc.size() - 1)) <= 500){
                totaldist += loc.distanceTo(prevLoc.get(prevLoc.size() - 1));
            }
        } catch(ArrayIndexOutOfBoundsException e){}
        prevLoc.add(loc);
        System.out.println(totaldist);
        binding.distance.setText("Distance: " + totaldist);
        //TODO: Find if address is new or not
        /*currentAddy = s;
        for(Address q: prevAddy){
            if(q.getAddressLine(0).equals(s.getAddressLine(0)))
        }
        if(!prevAddy.contains(s)){
            System.out.println(s);
            prevAddy.add(s);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fuse.removeLocationUpdates(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }
}
