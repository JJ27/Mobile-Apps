package com.example.lifecycledemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lifecycledemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);


        if(savedInstanceState != null){
            count = savedInstanceState.getInt("count");
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("LIFECYCLETAG", "onCreate() portrait");
            binding.textView.setText("count " + count);
            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("LIFECYCLETAG", "onClick()");
                    count++;
                    binding.textView.setText("count " + count);
                }
            });
        }
        else{
            Log.d("LIFECYCLETAG", "onCreate() landscape");
            binding.textView2.setText("count " + count);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("LIFECYCLETAG", "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        outState.putInt("count", count);
    }
    //onRestore done in oncreate method bc it's not guaranteed a call
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onStart(){
        Log.d("LIFECYCLETAG", "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d("LIFECYCLETAG", "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("LIFECYCLETAG", "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("LIFECYCLETAG", "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("LIFECYCLETAG", "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("LIFECYCLETAG", "onDestroy()");
        super.onDestroy();
    }
}