package com.example.clashroyale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MediaPlayer vp;
    MediaPlayer mp;
    SoundPool sp;
    GameSurface gs;
    SurfaceView sv;
    Thread gameThread;
    FrameLayout root;
    volatile boolean running = false;
    ArrayList<Integer> countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        root = (FrameLayout) findViewById(R.id.root);
        gs = new GameSurface(this);
        sv = new SurfaceView(this);
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
                    }
                });
                vp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        vp.stop();
                        //root.removeView(sv);
                        root.addView(gs);
                        gs.setZOrderOnTop(true);
                        gs.getHolder().setFormat(PixelFormat.TRANSPARENT);
                        //ImageView ig = new ImageView(getApplicationContext());
                        //ig.setImageResource(R.drawable.cr);
                        //root.addView(ig);
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
        root.addView(sv);

        sv.setZOrderOnTop(true);
        //gs.getHolder().setFormat(PixelFormat.TRANSPARENT);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.sudden_death_01);
        Runnable t = new Runnable() {
            @Override
            public void run() {
                //try {
                    //Thread.sleep(1000);
                    mp.start();
                //} catch (InterruptedException e) {
                    //e.printStackTrace();
                //}
            }
        };

        AudioAttributes ao = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        sp = new SoundPool.Builder().setMaxStreams(10).setAudioAttributes(ao).build();
        AssetManager am = this.getAssets();
        countdown = new ArrayList<>();
        for(int i = 0; i <= 10; i++){
            try {
                countdown.add(sp.load(am.openFd(i+"_cd_02.mp3"),1));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        try {
            gs.resume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {
        SurfaceHolder holder = getHolder();
        volatile float step = 0;
        volatile float a = 0;
        Bitmap ewiz, bg;
        int ballX = 0;
        Paint paint, rectpaint;
        int screenw;
        int screenh;
        Canvas canvas;
        int boundRight;
        int boundLeft;
        CountDownTimer timer;
        String timerText;
        boolean timerRun = false;



        public GameSurface(Context context) {
            super(context);
            System.out.println("Constructed!");
            ewiz = BitmapFactory.decodeResource(getResources(), R.drawable.ewiz);
            Display disp = getWindowManager().getDefaultDisplay();
            Point screenSize = new Point();
            disp.getSize(screenSize);

            screenw = screenSize.x;
            screenh = screenSize.y;
            bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cr), screenw, screenh, true);

            paint = new Paint();
            paint.setColor(Color.WHITE);
            rectpaint = new Paint();
            rectpaint.setColor(Color.GRAY);
            timerText = "0:30";

            timer = new CountDownTimer(31000, 1000) {
                @Override
                public void onTick(long l) {
                    timerText = "0:"+(l/1000);
                    if((l/100) >= 105 && (l/100) <= 110){
                        if(mp.isPlaying())
                            mp.setVolume(0.1f,0.1f);
                    } if((l/1000) <= 10){
                        if((l/1000) != 10)
                            timerText = "0:0"+(l/1000);
                        sp.play(countdown.get((int)(l/1000)),0.5f,0.5f,1,0,1);
                        //sp.autoPause();
                    }
                }

                @Override
                public void onFinish() {

                }
            };

            paint.setTypeface(getResources().getFont(R.font.crfont));

            SensorManager sm = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
            Sensor rot = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            sm.registerListener(this, rot, SensorManager.SENSOR_DELAY_FASTEST);
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.values[1] == 0)
                a = 0;
            else if(sensorEvent.values[1] <= -5)
                a = -1;
            else if(sensorEvent.values[1] <= 0)
                a = -0.5f;
            else if(sensorEvent.values[1] <= 5)
                a = 1;
            else
                a = 0.5f;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void run() {
            try {
                while (running) {
                    if (!holder.getSurface().isValid())
                        continue;
                    Canvas canvas = holder.lockCanvas();
                    if(!timerRun){
                        timer.start();
                        timerRun = true;
                    }
                    boundLeft = canvas.getClipBounds().left;
                    boundRight = canvas.getClipBounds().right;
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawBitmap(bg, 0,0, null);
                    canvas.drawRect(200,0,0,100,rectpaint);
                    if(Integer.parseInt(timerText.substring(2)) >= 30)
                        timerText = "0:30";
                    canvas.drawText(timerText, 100,70, paint);
                    paint.setTextSize(50);
                    System.out.println(step + " " + a);
                    step += a;
                    ballX += step;
                    if (ballX >= screenw / 2 - ewiz.getWidth() / 2) {
                        step = 0;
                        ballX = screenw / 2 - ewiz.getWidth() / 2;
                    } else if(ballX <= -1 * screenw / 2 + ewiz.getWidth() / 2){
                        step = 0;
                        ballX = -1 * screenw / 2 + ewiz.getWidth() / 2;
                    }
                    canvas.drawBitmap(ewiz, (screenw / 2) - ewiz.getWidth() / 2 + ballX, (screenh / 2) - ewiz.getHeight(), null);

                    holder.unlockCanvasAndPost(canvas);
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