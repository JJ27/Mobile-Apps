package com.example.conversionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.conversionproject.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            if(savedInstanceState != null){
                ArrayList<String> s = savedInstanceState.getStringArrayList("strings");
                binding.item1.setText(s.get(0));
                binding.unit1.setText(s.get(1));
                binding.item2.setText(s.get(2));
                binding.unit2.setText(s.get(3));
            }
            binding.convert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(containsEmpty(new TextView[]{binding.item1, binding.unit1, binding.unit2}))
                        binding.text1.setText("Fill in the first row, and unit on the second!");
                    else{
                        double ratio = asciiSum(binding.unit2) / asciiSum(binding.unit1);
                        try {
                            binding.item2.setText(("" + (Double.parseDouble(binding.item1.getText().toString()) * ratio)).substring(0, 4));
                        } catch(IndexOutOfBoundsException e){
                            binding.item2.setText("" + (Double.parseDouble(binding.item1.getText().toString()) * ratio));
                        }
                    }
                }
            });
        } else{
            if(savedInstanceState != null){
                ArrayList<String> s = savedInstanceState.getStringArrayList("strings");
                binding.litem2.setText(s.get(0));
                binding.lunit2.setText(s.get(1));
                binding.litem1.setText(s.get(2));
                binding.lunit1.setText(s.get(3));
            }
            binding.lconvert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(containsEmpty(new TextView[]{binding.litem2, binding.lunit2, binding.lunit1}))
                        binding.ltext1.setText("Fill in the first row, and unit on the second!");
                    else{
                        double ratio = asciiSum(binding.lunit1) / asciiSum(binding.lunit2);
                        try {
                            binding.litem1.setText(("" + (Double.parseDouble(binding.litem2.getText().toString()) * ratio)).substring(0, 4));
                        } catch(IndexOutOfBoundsException e){
                            binding.litem1.setText("" + (Double.parseDouble(binding.litem2.getText().toString()) * ratio));
                        }
                    }
                }
            });
        }
    }
    public boolean containsEmpty(TextView[] texts){
        for(int i = 0; i < texts.length; i++)
            if(texts[i].getText().toString().trim().isEmpty())
                return true;
        return false;
    }
    public double asciiSum(TextView t){
        double sum = 0;
        for(int i = 0; i < t.getText().length(); i++){
            sum += (int)t.getText().charAt(i);
        }
        return sum;
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            outState.putStringArrayList("strings", new ArrayList<String>(Arrays.asList(binding.item1.getText().toString(), binding.unit1.getText().toString(), binding.item2.getText().toString(),binding.unit2.getText().toString())));
        else
            outState.putStringArrayList("strings", new ArrayList<String>(Arrays.asList(binding.litem2.getText().toString(), binding.lunit2.getText().toString(), binding.litem1.getText().toString(),binding.lunit1.getText().toString())));
    }
}