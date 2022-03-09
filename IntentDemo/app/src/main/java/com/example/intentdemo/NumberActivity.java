package com.example.intentdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intentdemo.databinding.ActivityMainBinding;
import com.example.intentdemo.databinding.ActivityNumberBindingImpl;

public class NumberActivity extends AppCompatActivity {

    Button closeAct;
    EditText enteredNumber;

    private ActivityNumberBindingImpl binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        enteredNumber = findViewById(R.id.editText1);

        binding = DataBindingUtil.setContentView(NumberActivity.this, R.layout.activity_number);

        Bundle extras = getIntent().getExtras();
        String text1 = extras.getString("TEST");
        String text2 = extras.getString("TEST2");

        Toast.makeText(getApplicationContext(), text1+text2, Toast.LENGTH_SHORT).show();

        binding.closeAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFirstActivity(view);
            }
        });

    }

    private void callFirstActivity(View view) {
        Intent sendInfoBack = new Intent();
        sendInfoBack.putExtra(MainActivity.INTENT_CODE, enteredNumber.getText().toString());
        sendInfoBack.putExtra(MainActivity.OTHER_CODE, "324!");
        sendInfoBack.putExtra("HelloMotto", "DATADATA");
        setResult(RESULT_OK, sendInfoBack);
        finish();
    }
}