package com.example.gpsappa;


import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class GPSService extends Service implements LocationListener {
    private final Context context;
    private static final long updateDist = 5; //updates every 5 m
    private static final long updateTime = 30 * 1000; //updates every 30s
    protected LocationManager locationManager;
    private Location location;


    public GPSService(Context context, int type) {
        this.context = context;
        getCurrLocation(type);
    }

    public Location getCurrLocation(int type){
        try{
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if(type == 0) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    //no location possible??
                    throw new Exception("Network Location Not Found");
                } else {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateTime, updateDist, this);
                        Log.d("Access", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                }
            }
            turnGpsOn(context);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                if(location == null){
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateTime, updateDist, this);
                    Log.d("Access", "GPS");
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
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
        } catch(Exception e) {}
    }
    public void closeGPS(){
        if(locationManager != null)
            locationManager.removeUpdates(GPSService.this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //TODO: Display lat and long in layout from here
        this.location = location;
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
}
