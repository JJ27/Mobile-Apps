package com.example.criticalquestions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.criticalquestions.databinding.ActivityMainBinding;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = binding.rgroup.getCheckedRadioButtonId();
                if(selectedId != -1){
                    switch(((RadioButton) findViewById(selectedId)).getText().toString()){
                        case "Toast":
                            Toast.makeText(getApplicationContext(), "Toast Selected", Toast.LENGTH_SHORT).show();
                            break;
                        case "Change Color":
                            switch((int) (Math.random() * 3)){
                                case 0:
                                    binding.run.getRootView().setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.teal_200));
                                    break;
                                case 1:
                                    binding.run.getRootView().setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.purple_200));
                                    break;
                                case 2:
                                    binding.run.getRootView().setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green));
                                    break;
                                default:
                                    System.out.println("Math.random issue!");
                            }
                            break;
                        case "UPPERCASE":
                            binding.name.setText(binding.name.getText().toString().toUpperCase());
                    }
                }
            }
        });

        binding.launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TextActivity.class);
                if(binding.rgroup.getCheckedRadioButtonId() != -1)
                    i.putExtra("name", ((RadioButton) findViewById(binding.rgroup.getCheckedRadioButtonId())).getText().toString());
                else
                    i.putExtra("name", "None Selected");

                startActivity(i);
            }
        });

    }
}