package com.example.whackdeatheater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.whackdeatheater.databinding.ActivityBossBinding;
import com.example.whackdeatheater.databinding.ActivityBossBindingImpl;

public class BossActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);
        int score = getIntent().getIntExtra("score",0);
        TextView sd = findViewById(R.id.scoredisplay);
        Button pa = findViewById(R.id.playagain);
        sd.setText("Score: " + score);
        pa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BossActivity.this, SoundService.class);
                stopService(intent);
            }
        });
        Thread t = new Thread() {
            @Override
            public void run() {
                playBackgroundSound();
            }
        };
        t.start();
    }
    public void playBackgroundSound() {
        Intent intent = new Intent(BossActivity.this, SoundService.class);
        startService(intent);
    }
}