package com.example.whackdeatheater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityOptions;
import android.content.Intent;
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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TableRow;

import com.example.whackdeatheater.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity{
    ActivityMainBinding binding;
    final ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
    ScheduledFuture<?> cancelHandler;
    Runnable cancel;
    public static AtomicInteger timeLeft;
    final TranslateAnimation tA = new TranslateAnimation(0,0,200,0);
    ScheduledFuture<?> moleHandler;
    final TranslateAnimation tA2 = new TranslateAnimation(0,0,0,200);
    final TranslateAnimation tA3 = new TranslateAnimation(0,0,0,200);
    AtomicInteger score;
    static float offset = 0;
    static boolean voldy = false;
    static boolean ran = false;
    ArrayList<Integer> rows;
    ArrayList<Integer> cols;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new AtomicInteger(0);
        timeLeft = new AtomicInteger(60);
        setTimer();
        tA.setDuration(600);
        tA2.setDuration(600);
        tA3.setDuration(200);
        rows = new ArrayList<Integer>();
        cols = new ArrayList<Integer>();
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
                        if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.cup).getConstantState()){
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                            Intent intent = new Intent(MainActivity.this, BossActivity.class);
                            intent.putExtra("score", score.get());
                            startActivity(intent, options.toBundle());
                        }
                        if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.bellatrix).getConstantState()){
                            generateImage(R.drawable.bellatrix);
                        } else if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.igor).getConstantState()) {
                            generateImage(R.drawable.igor);
                        } else if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.lucmalfoynew).getConstantState()) {
                            generateImage(R.drawable.lucmalfoynew);
                        } else if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.voldy).getConstantState()) {
                            generateImage(R.drawable.voldy);
                        } else if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ran).getConstantState()) {
                            generateImage(R.drawable.ran);
                        }
                        if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.voldy).getConstantState()){
                            score.getAndAdd(10);
                            binding.score.setText("Score: " + score);
                        } else if(c.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ran).getConstantState()){
                            changeTime(-5);
                            score.addAndGet(-3);
                            binding.score.setText("Score: " + score);
                        } else
                            binding.score.setText("Score: " + score.addAndGet(1));
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
                            if(curr.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.cup).getConstantState())
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
                            try {
                                if (curr.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.cup).getConstantState())
                                    curr.setVisibility(View.INVISIBLE);
                            } catch(Exception e){e.printStackTrace();}
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
                        if(timeLeft.get() > 9)
                            binding.timedisplay.setText("0:" + timeLeft.get());
                        else
                            binding.timedisplay.setText("0:0" + timeLeft.get());
                        binding.progressBar.setProgress(timeLeft.get());
                        LayerDrawable progressBarDrawable = (LayerDrawable) binding.progressBar.getProgressDrawable();
                        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
                        Drawable progressDrawable = progressBarDrawable.getDrawable(1);
                        String hexback = "", hexfore = "";
                        if(timeLeft.get() <= 15){
                            //Gryffindor
                            hexback = "#D3A625";
                            hexfore = "#740001";
                        } else if(timeLeft.get() <= 30){
                            //Hufflepuff
                            hexback = "#000000";
                            hexfore = "#FFD800";
                        } else if(timeLeft.get() <= 45){
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
                timeLeft.getAndAdd(-1);
            }
        };
        ScheduledFuture<?> handler = service.scheduleAtFixedRate(timer, 1,1, TimeUnit.SECONDS);
        cancel = new Runnable() {
            @Override
            public void run() {
                int s = 4;
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
                tA.setDuration(1500);
                curr.startAnimation(tA);
                handler.cancel(false);
                moleHandler.cancel(false);
            }
        };
        cancelHandler = service.schedule(cancel, timeLeft.get(), TimeUnit.SECONDS);
    }
    public void changeTime(int z){
        cancelHandler.cancel(false);
        timeLeft.getAndAdd(z);
        if(timeLeft.get() < 0) timeLeft.set(0);
        cancelHandler = service.schedule(cancel, timeLeft.get(), TimeUnit.SECONDS);
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
            case 4:
                curr.setImageResource(R.drawable.cup);
                return;
        }
        if(((int)(Math.random() * 15)) == 1 && !voldy) {
            curr.setImageResource(R.drawable.voldy);
            voldy = true;
        } else if(((int)(Math.random() * 15)) == 2 && !ran){
            curr.setImageResource(R.drawable.ran);
            ran = true;
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

    public ImageView generateImage(int resid){
        ImageView image = new ImageView(this);
        image.setId(View.generateViewId());
        image.setAdjustViewBounds(true);
        Picasso.get().load(resid).fit().into(image);

        ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT*30, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT*30);
        image.setLayoutParams(params1);

        ConstraintLayout layout = binding.root;
        layout.addView(image);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(image.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        constraintSet.connect(image.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(image.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
        constraintSet.connect(image.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT);
        constraintSet.setVerticalBias(image.getId(), 0.13f + offset);
        constraintSet.setHorizontalBias(image.getId(), 0);

        offset += 0.01f;

        constraintSet.applyTo(layout);
        return image;
    }
}