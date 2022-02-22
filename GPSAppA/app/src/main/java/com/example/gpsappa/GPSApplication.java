package com.example.gpsappa;

import android.app.Application;
import android.location.Address;
import android.location.Location;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.List;

public class GPSApplication extends Application {
    private static GPSApplication app;

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    private List<String> addresses;
    private List<Location> locations;

    public List<String> getWayPointNames() {
        return wayPointNames;
    }

    public void setWayPointNames(List<String> wayPointNames) {
        this.wayPointNames = wayPointNames;
    }

    private List<String> wayPointNames;

    public List<Location> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Location> waypoints) {
        this.waypoints = waypoints;
    }

    private List<Location> waypoints;

    public List<Stopwatch> getTimes() {
        return times;
    }

    public void setTimes(List<Stopwatch> times) {
        this.times = times;
    }

    private List<Stopwatch> times;

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
        addresses = new ArrayList<String>();
        times = new ArrayList<Stopwatch>();
        waypoints = new ArrayList<Location>();
        wayPointNames = new ArrayList<String>();
    }
}
