package com.example.clashroyale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    MediaPlayer vp;
    MediaPlayer mp;
    SoundPool sp, sfx;
    GameSurface gs;
    SurfaceView sv;
    Thread gameThread;
    FrameLayout root;
    volatile boolean running = false;
    ArrayList<Integer> countdown, ending;
    int screenw, screenh, fireballhitsfx, nearmisssfx, ragesfx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        root = (FrameLayout) findViewById(R.id.root);
        Display disp = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        disp.getSize(screenSize);
        screenw = screenSize.x;
        screenh = screenSize.y;
        gs = new GameSurface(this);
        sv = new SurfaceView(this);
        root.addView(sv);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        rl.bringToFront();
        ImageView load = (ImageView) findViewById(R.id.load);
        Picasso.with(this).load(R.drawable.loading).resize(600, 200).into(load);
        load.bringToFront();
        ProgressBar pb = (ProgressBar) findViewById(R.id.loadbar);
        pb.setMax(100);
        pb.setProgress(0);
        pb.bringToFront();
        TextView percent = (TextView) findViewById(R.id.percent);
        percent.setTextSize(40);
        percent.setText("0%");
        percent.bringToFront();
        Thread loading = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.VISIBLE);
                        percent.setVisibility(View.VISIBLE);
                    }
                });
                int perc = 0;
                for(int i = 0; i < 100; i++){
                    perc += 1;
                    int finalPerc = perc;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.incrementProgressBy(1);
                            percent.setText(finalPerc + "%");
                        }
                    });
                    try {
                        TimeUnit.MILLISECONDS.sleep(13);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    vp = MediaPlayer.create(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.gameplay2), sv.getHolder());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                vp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mp.start();
                        vp.start();
                        loading.start();
                    }
                });
                vp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        vp.stop();
                        pb.setProgress(100);
                        percent.setText("100%");
                        root.addView(gs);
                        gs.setZOrderOnTop(true);
                        ImageView ig = new ImageView(getApplicationContext());
                        ig.setImageResource(R.drawable.cr);
                        root.addView(ig);
                        running = true;
                        gameThread = new Thread(gs);
                        gameThread.start();
                    }
                });
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });

        mp = MediaPlayer.create(getApplicationContext(), R.raw.sudden_death_01);
        mp.setVolume(0.9f,0.9f);
        Runnable t = new Runnable() {
            @Override
            public void run() {
                mp.start();
            }
        };

        AudioAttributes ao = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        sp = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(ao).build();
        AssetManager am = this.getAssets();
        countdown = new ArrayList<>();
        ending = new ArrayList<>();
        for(int i = 0; i <= 10; i++){
            try {
                countdown.add(sp.load(am.openFd(i+"_cd_02.mp3"),1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sfx = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(ao).build();
        try {
            fireballhitsfx = sfx.load(am.openFd("fire_ball_explo_02.mp3"),1);
            nearmisssfx = sfx.load(am.openFd("kill_enemy_big_summon_02.mp3"),1);
            ragesfx = sfx.load(am.openFd("rage_spell_01.mp3"),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ending.add(-5000);
        for(int i = 1; i <= 3; i++){
            try {
                ending.add(sfx.load(am.openFd(i+"_crown_01.mp3"),1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ending.add(sfx.load(am.openFd("scroll_win_02.mp3"),1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gs.pause();
        mp.stop();
        mp.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* unmute try {
            gs.resume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {
        SurfaceHolder holder = getHolder();
        volatile float step = 0;
        volatile float a = 0;
        Bitmap ewiz, bg, crown, crownholder, crown2;
        int ballX = 0;
        int ballY = -550;
        Paint paint, rectpaint;
        ArrayList<Obstacle> obstacles;
        Canvas canvas;
        int boundRight;
        int boundLeft;
        CountDownTimer timer;
        String timerText;
        boolean timerRun = false;
        boolean superdown = false;
        volatile ArrayList<Obstacle> currstrike;
        int[] crowndist;
        int crowns;
        Thread hit;
        boolean ended = true;

        public GameSurface(Context context) {
            super(context);
            ewiz = BitmapFactory.decodeResource(getResources(), R.drawable.ewiz);
            obstacles = new ArrayList<Obstacle>();
            bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cr), screenw, screenh, true);
            crown = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.crown), 100, 80, true);
            crown2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.crown), 150, 120, true);
            crownholder = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.crownholder), screenw, 350, true);
            crowndist = new int[]{-260, 113, 488, 900};
            crowns = 0;
            paint = new Paint();
            paint.setColor(Color.WHITE);
            rectpaint = new Paint();
            rectpaint.setColor(Color.GRAY);
            timerText = "0:40";
            hit = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(running) {
                        if(obstacles.size() > 0)
                            for (int i = obstacles.size() - 1; i > 0; i--) {
                                Obstacle ob = obstacles.get(i);
                                if(ob == null)
                                    continue;
                                if (ob.checkHit(ballX, ballY)) {
                                    if (!currstrike.contains(ob)) {
                                        sfx.autoPause();
                                        sfx.play(fireballhitsfx, 0.8f, 0.8f, 1, 0, 1);
                                        currstrike.add(ob);
                                        if(superdown)
                                            ballY -= 150;
                                        else
                                            ballY -= 120;
                                        ewiz = BitmapFactory.decodeResource(getResources(), R.drawable.ewizburnt);
                                    }
                                }
                                if ((ob.getY() - ob.getImg().getWidth()) <= (-1 * screenh / 2)) {
                                    obstacles.remove(ob);
                                    if (!currstrike.contains(ob))
                                        ballY += 100;
                                    else
                                        ewiz = BitmapFactory.decodeResource(getResources(), R.drawable.ewiz);
                                }
                            }
                    }
                }
            });

            currstrike = new ArrayList<Obstacle>();

            timer = new CountDownTimer(41000, 1000) {
                @Override
                public void onTick(long l) {
                    timerText = "0:"+(l/1000);
                    if((l/100) >= 105 && (l/100) <= 110){
                        if(mp.isPlaying())
                            mp.setVolume(0.07f,0.07f);
                    } if((l/1000) <= 10){
                        if((l/1000) != 10)
                            timerText = "0:0"+(l/1000);
                        sp.play(countdown.get((int)(l/1000)),0.8f,0.8f,1,0,1);
                    }
                }

                @Override
                public void onFinish() {
                    mp.stop();
                    running = false;
                }
            };

            paint.setTypeface(getResources().getFont(R.font.crfont));

            SensorManager sm = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
            Sensor rot = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            sm.registerListener(this, rot, SensorManager.SENSOR_DELAY_FASTEST);

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    superdown = !superdown;
                    if(superdown)
                        sfx.play(ragesfx, 1.2f, 1.2f, 1, 0, 1);
                }
            });
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(!superdown){
                if(sensorEvent.values[1] == 0)
                    a = 0;
                else if(sensorEvent.values[1] <= -5)
                    a = -1.2f;
                else if(sensorEvent.values[1] <= 0)
                    a = -0.7f;
                else if(sensorEvent.values[1] <= 5)
                    a = 1.2f;
                else
                    a = 0.7f;
            } else{
                if(sensorEvent.values[1] == 0)
                    a = 0;
                else if(sensorEvent.values[1] <= -5)
                    a = -1.5f;
                else if(sensorEvent.values[1] <= 0)
                    a = -1f;
                else if(sensorEvent.values[1] <= 5)
                    a = 1.5f;
                else
                    a = 1f;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void run() {
            try {
                while (true) {
                    if(running) {
                        if (!holder.getSurface().isValid())
                            continue;
                        Canvas canvas = holder.lockCanvas();
                        if (!timerRun) {
                            timer.start();
                            timerRun = true;
                            hit.start();
                        }
                        boundLeft = canvas.getClipBounds().left;
                        boundRight = canvas.getClipBounds().right;
                        paint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawBitmap(bg, 0, 0, null);
                        canvas.drawRect(200, 0, 0, 100, rectpaint);
                        canvas.drawBitmap(crown, screenw - 100, 150, null);
                        canvas.drawBitmap(crown, screenw - 100, 525, null);
                        canvas.drawBitmap(crown, screenw - 100, 900, null);
                        if (Integer.parseInt(timerText.substring(2)) >= 40)
                            timerText = "0:40";
                        canvas.drawText(timerText, 100, 70, paint);
                        paint.setTextSize(50);
                        step += a;
                        ballX += step;
                        if (ballX >= screenw / 2 - ewiz.getWidth() / 2) {
                            step = 0;
                            ballX = screenw / 2 - ewiz.getWidth() / 2;
                        } else if (ballX <= -1 * screenw / 2 + ewiz.getWidth() / 2) {
                            step = 0;
                            ballX = -1 * screenw / 2 + ewiz.getWidth() / 2;
                        }
                        if (ballY > (screenh / 2 - ewiz.getHeight()))
                            ballY = (screenh / 2) - ewiz.getHeight();
                        else if (ballY < (-1*(screenh / 2) + ewiz.getHeight()))
                            ballY = (-1*(screenh / 2)) + ewiz.getHeight();
                        canvas.drawBitmap(ewiz, (screenw / 2) - ewiz.getWidth() / 2 + ballX, (screenh / 2) - ewiz.getHeight() - ballY, null);
                        if (Math.random() * 1000 <= 300) {
                            if (obstacles.size() <= ((int) (Math.random() * 2 + 1))) {
                                obstacles.add(new Obstacle(getApplicationContext(), ((int) (Math.random() * (screenw - (2 * 60))) - screenw / 2), R.drawable.fireball));
                            }
                        }
                        for (int i = obstacles.size() - 1; i > 0; i--) {
                            Obstacle ob = obstacles.get(i);
                            if (superdown)
                                ob.superdown();
                            else
                                ob.down();
                            if (ballY >= crowndist[crowns]) {
                                crowns += 1;
                                sfx.play(nearmisssfx, 0.8f, 0.8f, 1, 0, 1);
                            }
                            canvas.drawBitmap(ob.getImg(), (screenw / 2) - ob.getImg().getWidth() / 2 + ob.getX(), (screenh / 2) - ob.getImg().getHeight() - ob.getY(), null);
                        }
                        holder.unlockCanvasAndPost(canvas);
                    } else if (ended){
                        TimeUnit.MILLISECONDS.sleep(1000);
                        if (!holder.getSurface().isValid())
                            continue;
                        Canvas canvas = holder.lockCanvas();
                        canvas.drawBitmap(bg, 0, 0, null);
                        canvas.drawBitmap(crownholder, 0, screenh/2-(350/2),null);
                        for(int i = 1; i <= crowns; i++){
                            if(i >= 1)
                                canvas.drawBitmap(crown2, 167,670, null);
                            if (i >= 2)
                                canvas.drawBitmap(crown2, 464,660, null);
                            if(i == 3)
                                canvas.drawBitmap(crown2, 759,670, null);
                            holder.unlockCanvasAndPost(canvas);
                            canvas = holder.lockCanvas();
                            canvas.drawBitmap(bg, 0, 0, null);
                            canvas.drawBitmap(crownholder, 0, screenh/2-(350/2),null);
                            sfx.autoPause();
                            sfx.play(ending.get(i),1.0f,1.0f,1,0,1);
                            TimeUnit.MILLISECONDS.sleep(500);
                        }
                        sfx.autoPause();
                        TimeUnit.MILLISECONDS.sleep(500);
                        sfx.play(ending.get(4),1.0f,1.0f,1,0,1);
                        ended = false;
                    }
                }
            } catch(Exception e){e.printStackTrace();}
        }

        public void resume() throws InterruptedException {
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause(){
            running = false;
            while(true){
                try{
                    gameThread.join();
                } catch (InterruptedException e){ }
            }
        }
    }
}