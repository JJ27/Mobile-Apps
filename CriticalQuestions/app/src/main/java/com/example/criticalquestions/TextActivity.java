package com.example.criticalquestions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.criticalquestions.databinding.ActivityTextBinding;

public class TextActivity extends AppCompatActivity {
    ActivityTextBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_text);
        binding.display.setText(getIntent().getExtras().getString("name"));
    }
}