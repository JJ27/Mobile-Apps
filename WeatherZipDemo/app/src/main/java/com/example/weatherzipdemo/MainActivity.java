package com.example.weatherzipdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.example.weatherzipdemo.databinding.ActivityMainBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    String zip;
    URL url;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ObjectMapper mapper = new ObjectMapper();

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JsonNode rootNode = mapper.readTree(run());
                    binding.lat.setText("Lat: " + rootNode.findValue("lat").asText());
                    binding.longitude.setText("Long: " + rootNode.findValue("lon").asText());
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String run() throws MalformedURLException {
        zip = binding.enterzip.getText().toString();
        try {
            url = new URL("http://api.openweathermap.org/geo/1.0/zip?zip="+ zip + ",US&appid=2fc9adf729b289e256d2bf14889cc922");
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
            return buff.readLine();
        } catch (IOException e) {
            binding.enterzip.setHint("Enter A Valid US Zip");
            e.printStackTrace();
        }
        return "";
    }
}