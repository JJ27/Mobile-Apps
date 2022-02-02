package com.example.gpsappa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import com.example.gpsappa.databinding.ActivityMainBinding;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private GPSService gpsService;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        geocoder = new Geocoder(MainActivity.this);
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
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

    public void getLoc() throws IOException {
        gpsService = new GPSService(MainActivity.this, 1);
        binding.lat.setText("Latitude: " + String.valueOf(gpsService.getLocation().getLatitude()));
        binding.lon.setText("Longitude: " + String.valueOf(gpsService.getLocation().getLongitude()));
        Address address = geocoder.getFromLocation(gpsService.getLocation().getLatitude(), gpsService.getLocation().getLongitude(),1).get(0);
        binding.address.setText("Address: " + address.getAddressLine(0).trim());
    }
}