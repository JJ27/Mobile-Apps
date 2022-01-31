package com.example.weatherappb;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.weatherappb.databinding.ActivityMainBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class APICall extends IntentService {

    public APICall(){
        super("APICall");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String zip = intent.getStringExtra("zipcode");
        ObjectMapper mapper = new ObjectMapper();
        Intent broadcast = new Intent();
        broadcast.setAction("Received");
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            URL url = new URL("http://api.openweathermap.org/geo/1.0/zip?zip="+ zip + ",US&appid=2fc9adf729b289e256d2bf14889cc922");
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
            JsonNode rootNode = mapper.readTree(buff.readLine());
            broadcast.putExtra("city",rootNode.findValue("name").toString());
            broadcast.putExtra("lat", rootNode.findValue("lat").toString().substring(0,5));
            broadcast.putExtra("lon", rootNode.findValue("lon").toString().substring(0,5));
            URL url2 = new URL("http://api.openweathermap.org/data/2.5/onecall?lat="+ rootNode.findValue("lat") + "&lon="+ rootNode.findValue("lon") + "&exclude=minutely,daily,alerts&units=imperial&appid=2fc9adf729b289e256d2bf14889cc922");
            URLConnection connection2 = url2.openConnection();
            InputStream stream2 = connection2.getInputStream();
            BufferedReader buff2 = new BufferedReader(new InputStreamReader(stream2));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = buff2.readLine()) != null)
                sb.append(line);
            broadcast.putExtra("json", sb.toString());
        } catch (IOException e) {
            broadcast.putExtra("json","ZIPCODEWRONG");
            e.printStackTrace();
        }
        sendBroadcast(broadcast);
    }
}
