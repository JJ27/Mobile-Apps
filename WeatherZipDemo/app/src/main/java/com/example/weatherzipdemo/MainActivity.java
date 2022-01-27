package com.example.weatherzipdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.example.weatherzipdemo.databinding.ActivityMainBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

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
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JsonNode g = run();
                    binding.lat.setText("Lat: " + rootNode.findValue("lat").asText());
                    binding.longitude.setText("Long: " + rootNode.findValue("lon").asText());
                    Log.d("tag",("https://raw.githubusercontent.com/isneezy/open-weather-icons/23fbe8a68faa04fa85003d15525d6b1ada746c58/src/svg/" + g.findValue("weather").findValue("icon") +".svg").replaceAll("\"",""));
                    Picasso.with(getApplicationContext()).load(("https://raw.githubusercontent.com/isneezy/open-weather-icons/23fbe8a68faa04fa85003d15525d6b1ada746c58/src/svg/" + g.findValue("weather").findValue("icon") +".svg").replaceAll("\"","")).resize(600,600).into(binding.imageView);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JsonNode run() throws MalformedURLException {
        zip = binding.enterzip.getText().toString();
        try {
            url = new URL("http://api.openweathermap.org/geo/1.0/zip?zip="+ zip + ",US&appid=2fc9adf729b289e256d2bf14889cc922");
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
            rootNode = mapper.readTree(buff.readLine());
            URL url2 = new URL("http://api.openweathermap.org/data/2.5/onecall?lat="+ rootNode.findValue("lat") + "&lon="+ rootNode.findValue("lon") + "&exclude=daily&appid=2fc9adf729b289e256d2bf14889cc922");
            URLConnection connection2 = url2.openConnection();
            InputStream stream2 = connection2.getInputStream();
            BufferedReader buff2 = new BufferedReader(new InputStreamReader(stream2));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = buff2.readLine()) != null)
                sb.append(line);
            return mapper.readTree(sb.toString());
        } catch (IOException e) {
            binding.enterzip.setHint("Enter A Valid US Zip");
            e.printStackTrace();
        }
        return rootNode;
    }
}