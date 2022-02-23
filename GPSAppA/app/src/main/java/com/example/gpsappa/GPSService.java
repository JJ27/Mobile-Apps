package com.example.gpsappa;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.gpsappa.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GPSService extends AppCompatActivity {
    private final Context context;
    ActivityMainBinding binding;
    public static FusedLocationProviderClient fuse;
    public static LocationCallback callback;
    public static LocationRequest locationRequest;
    Location currloc;
    String current, recent, fav;
    List<String> prevAddy;
    List<Stopwatch> times;
    List<Location> prevLoc;
    List<Location> waypoints;
    List<String> waypointNames;
    ArrayList<String> recents;
    ArrayList<Stopwatch> recenttimes;
    float totaldist;

    public float getTotaldist() {
        return totaldist;
    }
    public String getCurrent() {
        return current;
    }
    public String getFav() {
        return fav;
    }
    public String getRecent() {
        return recent;
    }
    public Location getCurrloc() {
        return currloc;
    }
    public ArrayList<String> getRecents() {
        return recents;
    }
    public ArrayList<Stopwatch> getRecenttimes() {
        return recenttimes;
    }

    @SuppressLint("MissingPermission")
    public GPSService(Context context, int type, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
        this.totaldist = 0f;
        recents = new ArrayList<String>();
        recenttimes = new ArrayList<Stopwatch>();
        fav = null;
        fuse = LocationServices.getFusedLocationProviderClient(context);
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUI(locationResult.getLastLocation());
            }
        };
        locationRequest = LocationRequest.create().setInterval(1000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(1);
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    public GPSService(Context context, int type, ActivityMainBinding binding, float dist, String current, String fav, String rec, ArrayList<String> recs) {
        this.context = context;
        this.binding = binding;
        this.totaldist = dist;
        this.current = current;
        this.fav = fav;
        this.recent = rec;
        this.recents = recs;
        fuse = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else{
        }
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUI(locationResult.getLastLocation());
            }
        };
        locationRequest = LocationRequest.create().setInterval(1000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(1);
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    public void updateUI(Location loc){
        binding.lat.setText("Latitude: " + loc.getLatitude());
        binding.lon.setText("Longitude: " + loc.getLongitude());
        currloc = loc;
        Geocoder gc = new Geocoder(context);
        GPSApplication app = (GPSApplication) context.getApplicationContext();
        times = app.getTimes();
        prevLoc = app.getLocations();
        prevAddy = app.getAddresses();
        recenttimes = app.getRecenttimes();
        Runnable count = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.currtime.setText("Time: " + times.get(prevAddy.indexOf(current)).elapsed(TimeUnit.SECONDS) + " s");
                            }
                        });
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(count).start();
        try {
            try{times.get(prevAddy.indexOf(current)).stop();} catch(ArrayIndexOutOfBoundsException | IllegalStateException e){e.printStackTrace();}
            if(fav == null)
                fav = current;
            else
                try{if(times.get(prevAddy.indexOf(current)).elapsed(TimeUnit.MILLISECONDS) > times.get(prevAddy.indexOf(fav)).elapsed(TimeUnit.MILLISECONDS))
                    fav = current;} catch(ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
            try{
            if(!current.equals(gc.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1).get(0).getAddressLine(0))){
                recent = current;
                System.out.println("if");
                current = gc.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1).get(0).getAddressLine(0);
                if(recent != null && !recent.equals(current) && (times.get(prevAddy.indexOf(recent)).elapsed(TimeUnit.MILLISECONDS) >= 2)) {
                    System.out.println("inner");
                    System.out.println("Recent: " + recent);
                    recents.add(0, recent);
                    binding.recent.setText(recent);
                    try {
                        recenttimes.add(0, times.get(prevAddy.indexOf(recent)));
                        binding.recent.setText(binding.recent.getText().toString() + " (" + times.get(prevAddy.indexOf(recent)).elapsed(TimeUnit.SECONDS) + " s)");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            } else{
                System.out.println("else");
                binding.recent.setText(recent);
                System.out.println("Recent: " + recent);
                if(recent != null && (times.get(prevAddy.indexOf(recent)).elapsed(TimeUnit.MILLISECONDS) >= 2))
                    recents.add(0, recent);
                try {
                    binding.recent.setText(binding.recent.getText().toString() + " (" + times.get(prevAddy.indexOf(recent)).elapsed(TimeUnit.SECONDS) + " s)");
                    if((times.get(prevAddy.indexOf(recent)).elapsed(TimeUnit.MILLISECONDS) >= 2))
                        recenttimes.add(0, times.get(prevAddy.indexOf(recent)));
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }} catch (NullPointerException e){e.printStackTrace();}
            try{binding.fav.setText(fav  + " (" + times.get(prevAddy.indexOf(fav)).elapsed(TimeUnit.SECONDS) + " s)");} catch (ArrayIndexOutOfBoundsException e){e.printStackTrace();}
            current = gc.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1).get(0).getAddressLine(0);
            if(prevAddy.contains(current)){
                if(!times.get(prevAddy.indexOf(current)).isRunning())
                    times.get(prevAddy.indexOf(current)).start();
            } else{
                prevAddy.add(current);
                times.add(Stopwatch.createStarted(
                        new Ticker() {
                            public long read() {
                                return android.os.SystemClock.elapsedRealtimeNanos();
                            }}));
            }
            binding.address.setText(current);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(!Build.FINGERPRINT.contains("generic")) {
                if (loc.distanceTo(prevLoc.get(prevLoc.size() - 1)) <= 500) {
                    System.out.println(loc.distanceTo(prevLoc.get(prevLoc.size() - 1)));
                    totaldist += loc.distanceTo(prevLoc.get(prevLoc.size() - 1));
                }
            } else{
                System.out.println(loc.distanceTo(prevLoc.get(prevLoc.size() - 1)));
                totaldist += loc.distanceTo(prevLoc.get(prevLoc.size() - 1));
            }
        } catch(ArrayIndexOutOfBoundsException e){}
        prevLoc.add(loc);
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

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    public void setWaypoint(String name) {
        GPSApplication app = (GPSApplication) context.getApplicationContext();
        waypoints = app.getWaypoints();
        waypointNames = app.getWayPointNames();
        if(currloc != null) {
            waypoints.add(currloc);
            waypointNames.add(name);
        }
    }
}
