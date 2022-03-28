package com.example.whackdeatheater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import com.example.whackdeatheater.databinding.ActivityMainBinding;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> cancelHandler;
    Runnable cancel;
    public static int timeLeft = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTimer();

    }
    public void setTimer(){
        Runnable timer;
        timer = new Runnable() {
            @Override
            public void run() {
                System.out.println(timeLeft);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(timeLeft > 9)
                            binding.timedisplay.setText("0:" + timeLeft);
                        else
                            binding.timedisplay.setText("0:0" + timeLeft);
                        binding.progressBar.setProgress(timeLeft);
                        LayerDrawable progressBarDrawable = (LayerDrawable) binding.progressBar.getProgressDrawable();
                        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
                        Drawable progressDrawable = progressBarDrawable.getDrawable(1);
                        String hexback = "", hexfore = "";
                        if(timeLeft <= 15){
                            //Ravenclaw
                            hexback = "#946B2D";
                            hexfore = "#0E1A40";
                        } else if(timeLeft <= 30){
                            //Hufflepuff
                            hexback = "#000000";
                            hexfore = "#FFD800";
                        } else if(timeLeft <= 45){
                            //Slytherin
                            hexback = "#5D5D5D";
                            hexfore = "#1A472A";
                        } else {
                            //Gryffindor
                            hexback = "#D3A625";
                            hexfore = "#740001";
                        }

                        backgroundDrawable.setTint(Color.parseColor(hexback));
                        //binding.progressBar.setBackgroundColor(Color.parseColor(hexback));
                        binding.progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(hexfore)));
                    }
                });
                timeLeft--;
            }
        };
        ScheduledFuture<?> handler = service.scheduleAtFixedRate(timer, 1,1, TimeUnit.SECONDS);
        cancel = () -> handler.cancel(false);
        cancelHandler = service.schedule(cancel, timeLeft, TimeUnit.SECONDS);
    }
    public void changeTime(int z){
        cancelHandler.cancel(false);
        timeLeft += z;
        if(timeLeft < 0) timeLeft = 0;
        cancelHandler = service.schedule(cancel, timeLeft, TimeUnit.SECONDS);
    }
}