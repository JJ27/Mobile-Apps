package com.example.spinnerpractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.spinnerpractice.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Set Prefix Spinner
        setSpinner(binding.selector, new ArrayList<String>(Arrays.asList("Sen.", "Rep.", "Hon.")));
        setSpinner(binding.names, new ArrayList<String>());

        //Button Mechanism
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String r = binding.selector.getSelectedItem().toString() + " " + binding.input.getText().toString();
                binding.display.setText(r);
                ArrayList<String> result = getItems(binding.names);
                result.add(r);
                setSpinner(binding.names, result);
            }
        });
    }
    public void setSpinner(Spinner spin, ArrayList<String> strings){
        ArrayAdapter<String> selectorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        selectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(selectorAdapter);
    }
    public ArrayList<String> getItems(Spinner spinner){
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < spinner.getAdapter().getCount(); i++)
            result.add(spinner.getAdapter().getItem(i).toString());
        return result;
    }
}