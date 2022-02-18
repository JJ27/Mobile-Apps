package com.example.gpsappa;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.gpsappa.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private GPSService gpsService;

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
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getLoc();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> { });

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            gpsService = new GPSService(MainActivity.this, 1, binding, savedInstanceState.getFloat("dist"), savedInstanceState.getString("curr"), savedInstanceState.getString("fav"), savedInstanceState.getString("rec"));
        } catch (NullPointerException e) {
            try {
                getLoc();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            GPSService.fuse.removeLocationUpdates(GPSService.callback);
            outState.putFloat("dist", gpsService.getTotaldist());
            outState.putString("curr", gpsService.getCurrent());
            outState.putString("fav", gpsService.getFav());
            outState.putString("rec", gpsService.getRecent());
        } catch(NullPointerException e){}
    }

    public void getLoc() throws IOException {
        gpsService = new GPSService(MainActivity.this, 1, binding);
        /*binding.lat.setText("Latitude: " + String.valueOf(gpsService.getLocation().getLatitude()));
        binding.lon.setText("Longitude: " + String.valueOf(gpsService.getLocation().getLongitude()));
        Address address = geocoder.getFromLocation(gpsService.getLocation().getLatitude(), gpsService.getLocation().getLongitude(),1).get(0);
        binding.address.setText("Address: " + address.getAddressLine(0).trim());*/
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
                    Toast.makeText(this, "This app requires location permissions!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}