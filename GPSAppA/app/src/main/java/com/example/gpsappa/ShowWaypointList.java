package com.example.gpsappa;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowWaypointList extends AppCompatActivity {
    ListView waypointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_waypoint_list);

        GPSApplication app = (GPSApplication) getApplicationContext();
        List<Location> waypoints = app.getWaypoints();
        waypointList = findViewById(R.id.waypointlist);
        waypointList.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, waypoints));
    }
}