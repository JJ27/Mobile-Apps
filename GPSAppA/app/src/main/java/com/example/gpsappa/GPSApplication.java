package com.example.gpsappa;

import android.app.Application;
import android.location.Address;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class GPSApplication extends Application {
    private static GPSApplication app;

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    private List<Address> addresses;
    private List<Location> locations;

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public GPSApplication getInstance(){
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        locations = new ArrayList<Location>();
        addresses = new ArrayList<Address>();
    }
}
