package com.example.gpsappa;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class APICall extends IntentService {

    public APICall() {
        super("APICall");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Double lat = intent.getDoubleExtra("lat",-34);
        Double lon = intent.getDoubleExtra("lon", 121);
        Double destlat = intent.getDoubleExtra("destlat", 0);
        Double destlon = intent.getDoubleExtra("destlon", 0);
        ObjectMapper mapper = new ObjectMapper();
        Intent broadcast = new Intent();
        broadcast.setAction("Received");
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lon + "&destination=" + destlat + "," + destlon + "&key=AIzaSyDfN0N_1sFGr6l-3U2yTl0ZWfN5Qv3g5pA");
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = buff.readLine()) != null)
                sb.append(line);
            JsonNode rootNode = mapper.readTree(sb.toString());
            MapsActivity.setPolyline(rootNode.findValue("overview_polyline").findValue("points").toString());
            JsonNode bound = rootNode.findValue("routes").findValue("bounds");
            MapsActivity.setBounds(new LatLng(bound.findValue("southwest").findValue("lat").asDouble(), bound.findValue("southwest").findValue("lng").asDouble())
            , new LatLng(bound.findValue("northeast").findValue("lat").asDouble(), bound.findValue("southwest").findValue("lng").asDouble()));
        } catch (IOException e) {
            broadcast.putExtra("polyline", "ZIPCODEWRONG");
            e.printStackTrace();
        }
        sendBroadcast(broadcast);
    }
}
