package com.example.gpsappa;

import static com.example.gpsappa.GPSService.fuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gpsappa.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private GPSService gpsService;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.addwaypoint:
                DialogFragment zc = new WaypointFragment("Waypoint Name:");
                zc.show(getSupportFragmentManager(),"Hello");
                getSupportFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        if(requestKey.equals("requestKey")) {
                            String name = result.getString("name");
                            gpsService.setWaypoint(name);
                        }
                    }
                });
                break;
            case R.id.getloc:
                try {
                    getLoc();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.startmap:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                break;
            case R.id.startroute:
                gpsService.setWaypoint("Origin");
                DialogFragment z2 = new WaypointFragment("Destination Name:");
                z2.show(getSupportFragmentManager(),"Hello2");
                getSupportFragmentManager().setFragmentResultListener("requestOther", MainActivity.this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        GPSApplication app = (GPSApplication)getApplicationContext();
                        List<Location> waypoints = app.getWaypoints();
                        List<String> waypointNames = app.getWayPointNames();
                        try {
                            Location waypoint = waypoints.get(waypointNames.indexOf(result.getString("name")));
                            Intent apiIntent = new Intent(MainActivity.this, APICall.class);
                            apiIntent.putExtra("lat", gpsService.getCurrloc().getLatitude());
                            apiIntent.putExtra("lon", gpsService.getCurrloc().getLongitude());
                            apiIntent.putExtra("destlat", waypoint.getLatitude());
                            apiIntent.putExtra("destlon", waypoint.getLongitude());
                            startService(apiIntent);
                        } catch(Exception e){
                            Toast.makeText(MainActivity.this, "Invalid WayPoint", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Denied");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            gpsService = new GPSService(MainActivity.this, 1, binding, savedInstanceState.getFloat("dist"), savedInstanceState.getString("curr"), savedInstanceState.getString("fav"), savedInstanceState.getString("rec"), savedInstanceState.getStringArrayList("recs"));
        } catch (NullPointerException e) {
            try {
                getLoc();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(GPSService.fuse != null)
            GPSService.fuse.removeLocationUpdates(GPSService.callback);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if(fuse != null)
            fuse.requestLocationUpdates(GPSService.locationRequest, GPSService.callback, Looper.getMainLooper());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            fuse.removeLocationUpdates(GPSService.callback);
            outState.putFloat("dist", gpsService.getTotaldist());
            outState.putString("curr", gpsService.getCurrent());
            outState.putString("fav", gpsService.getFav());
            outState.putString("rec", gpsService.getRecent());
            outState.putStringArrayList("recs", gpsService.getRecents());
        } catch(NullPointerException e){}
    }

    public void getLoc() throws IOException {
        gpsService = new GPSService(MainActivity.this, 1, binding);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 101:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    try {
                        getLoc();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    Toast.makeText(this, "This app requires location permissions!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
}