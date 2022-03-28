package com.example.whackdeatheater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TableRow;

import com.example.whackdeatheater.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    ActivityMainBinding binding;
    final ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
    ScheduledFuture<?> cancelHandler;
    Runnable cancel;
    public static int timeLeft = 60;
    ArrayList<Integer> rows = new ArrayList<Integer>();
    ArrayList<Integer> cols = new ArrayList<Integer>();
    final TranslateAnimation tA = new TranslateAnimation(0,0,200,0);
    ScheduledFuture<?> moleHandler;
    final TranslateAnimation tA2 = new TranslateAnimation(0,0,0,200);
    final TranslateAnimation tA3 = new TranslateAnimation(0,0,0,200);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTimer();
        tA.setDuration(600);
        tA2.setDuration(600);
        tA3.setDuration(200);
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int finalI = i;
                int finalJ = j;
                findImage(i,j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("onclick");
                        ImageView c = findImage(finalI, finalJ);
                        System.out.println(c);
                        c.clearAnimation();
                        c.startAnimation(tA3);
                    }
                });
            }
        }
        moleHandler = service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    int s = (int) (Math.random() * 3) + 1;
                    int row, col;
                    do {
                        row = (int) (Math.random() * 3);
                        col = (int) (Math.random() * 3);
                    } while (rows.contains(row) && cols.contains(col));
                    rows.add(row);
                    cols.add(col);
                    ImageView curr = findImage(row, col);
                    System.out.println("i" + row + "" + col);
                    MainActivity.this.runOnUiThread(() -> runMole(s, curr));
                    System.out.println("RunMole");
                    curr.startAnimation(tA);
                    tA.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            curr.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //long currtime = System.nanoTime();
                            //while(System.nanoTime() <= (currtime + 100)){}
                            curr.startAnimation(tA2);
                            rows.remove(findRow(curr));
                            cols.remove(findCol(curr));
                            curr.setVisibility(View.INVISIBLE);
                            System.out.println("j"+findRow(curr)+findCol(curr));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    tA2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            curr.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    rows.remove(Integer.valueOf(findRow(curr)));
                    cols.remove(Integer.valueOf(findCol(curr)));
                } catch (Exception e) {e.printStackTrace();}
            }
        }, 800, 1100, TimeUnit.MILLISECONDS);
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
                        android.os.Process.setThreadPriority(Thread.MAX_PRIORITY);
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
                            //Gryffindor
                            hexback = "#D3A625";
                            hexfore = "#740001";
                        } else if(timeLeft <= 30){
                            //Hufflepuff
                            hexback = "#000000";
                            hexfore = "#FFD800";
                        } else if(timeLeft <= 45){
                            //Slytherin
                            hexback = "#5D5D5D";
                            hexfore = "#1A472A";
                        } else {
                            //Ravenclaw
                            hexback = "#946B2D";
                            hexfore = "#0E1A40";

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
        cancel = new Runnable() {
            @Override
            public void run() {
                handler.cancel(false);
                moleHandler.cancel(false);
            }
        };
        cancelHandler = service.schedule(cancel, timeLeft, TimeUnit.SECONDS);
    }
    public void changeTime(int z){
        cancelHandler.cancel(false);
        timeLeft += z;
        if(timeLeft < 0) timeLeft = 0;
        cancelHandler = service.schedule(cancel, timeLeft, TimeUnit.SECONDS);
    }

    public void runMole(int s, ImageView curr){
        switch(s){
            case 1:
                curr.setImageResource(R.drawable.bellatrix);
                break;
            case 2:
                curr.setImageResource(R.drawable.lucmalfoynew);
                break;
            case 3:
                curr.setImageResource(R.drawable.igor);
                break;
        }
    }
    public ImageView findImage(int row, int col){
        return (ImageView) findViewById(getResources().getIdentifier("i" + row + "" + col, "id", getApplicationContext().getPackageName()));
    }
    public CardView findCard(int row, int col){
        return (CardView) (findViewById(getResources().getIdentifier("i" + row + "" + col, "id", getApplicationContext().getPackageName()))).getParent();
    }
    public Integer findRow(ImageView img){
        return Integer.valueOf(binding.tableLayout.indexOfChild((TableRow) img.getParent().getParent().getParent()));
    }
    public Integer findCol(ImageView img){
        return Integer.valueOf(((ViewGroup) img.getParent().getParent().getParent()).indexOfChild((ConstraintLayout) img.getParent().getParent()));
    }
}