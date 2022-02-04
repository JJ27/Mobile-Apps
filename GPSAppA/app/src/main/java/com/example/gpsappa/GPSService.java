package com.example.gpsappa;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.gpsappa.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.List;

public class GPSService extends Service implements LocationListener {
    private final Context context;
    private static final long updateDist = 1; //updates every 1 m
    private static final long updateTime = 1 * 1000; //updates every 1s
    protected LocationManager locationManager;
    private Location location;
    ActivityMainBinding binding;


    public GPSService(Context context, int type, ActivityMainBinding binding) {
        this.context = context;
        this.location = getCurrLocation();
        this.binding = binding;
    }

    private Location getCurrLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        turnGpsOn(context);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        System.out.println("getCurrLocation()");
        for (String provider : providers) {
            System.out.println(provider);
            if(provider.equals("passive"))
                continue;
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("No Permissions");
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null)
                continue;
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                closeGPS();
                locationManager.requestLocationUpdates(provider, updateTime, updateDist, this);
                System.out.println(provider);
                bestLocation = l;
            }
        }
        return bestLocation;
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
}
