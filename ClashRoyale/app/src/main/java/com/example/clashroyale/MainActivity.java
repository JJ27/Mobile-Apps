package com.example.clashroyale;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mp;
    VideoView vw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.sudden_death_01);
        Runnable t = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    mp.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        vw = findViewById(R.id.videoView);
        vw.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.gameplay));
        vw.start();
        vw.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                mp.setLooping(true);
                t.run();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
        mp.release();
    }
}