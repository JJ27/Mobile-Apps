package com.example.jsondemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String str = "{\"name\":\"John\", \"age\":31}";


        tv = findViewById(R.id.tv);
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(str)) {

        } catch(JSONException | IOException e){
            e.printStackTrace();
        }

        JSONObject schoolInfo = new JSONObject();


    }
}