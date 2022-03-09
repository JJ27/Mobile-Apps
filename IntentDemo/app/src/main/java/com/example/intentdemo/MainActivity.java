package com.example.intentdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.intentdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    static final int NUMBER_CODE = 1234;
    static String INTENT_CODE = "number";
    static String OTHER_CODE = "nummy";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);



        binding.launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSecondActivity(view);
            }
        });
    }

    private void callSecondActivity(View view) {
        Intent intentToLoad = new Intent(MainActivity.this, NumberActivity.class);
        //use key value pairs to save and retrieve values
        intentToLoad.putExtra("TEST", "This is a test");
        intentToLoad.putExtra("TEST2", "Teeeeeest!");

        //startActivity(intentToLoad);
        //need to override onActivityResult that is invoked automatically when 2nd activity returns result
        startActivityForResult(intentToLoad, NUMBER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NUMBER_CODE && resultCode == RESULT_OK){
            String one = data.getStringExtra(INTENT_CODE);
            String two = data.getStringExtra(OTHER_CODE);
            String three = data.getStringExtra("HelloMotto");
            binding.textView.setText(one+two+three);
        }
    }
}