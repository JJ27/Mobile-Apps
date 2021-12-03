package com.example.orientationstuff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.orientationstuff.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

       // setSpinner(binding.spinner, new ArrayList<String>(Arrays.asList("Democratic", "Republican", "Libertarian", "Green")));

       // if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
         //   setSpinner(binding.spinner, new ArrayList<String>(Arrays.asList("Democratic", "Republican", "Libertarian", "Green")));
    }
    public void setSpinner(Spinner spin, ArrayList<String> strings){
        ArrayAdapter<String> selectorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        selectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(selectorAdapter);
    }
}