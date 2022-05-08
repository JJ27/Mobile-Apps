package com.example.clashroyale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    MediaPlayer vp;
    MediaPlayer mp;
    GameSurface gs;
    SurfaceView sv;
    Thread gameThread;
    volatile boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout root = findViewById(R.id.root);
        gs = new GameSurface(this);
        sv = new SurfaceView(this);
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    vp = MediaPlayer.create(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.gameplay2), sv.getHolder());
                } catch(Exception e){
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
        root.addView(sv);

        sv.setZOrderOnTop(true);
        //gs.getHolder().setFormat(PixelFormat.TRANSPARENT);

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
        Bitmap ball;
        int ballX = 0;
        Paint paint;
        int screenw;
        int screenh;
        Canvas canvas;
        int boundRight;
        int boundLeft;
        volatile float a = 0;



        public GameSurface(Context context) {
            super(context);
            System.out.println("Constructed!");
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            Display disp = getWindowManager().getDefaultDisplay();
            Point screenSize = new Point();
            disp.getSize(screenSize);

            screenw = screenSize.x;
            screenh = screenSize.y;

            paint = new Paint();
            getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

                }
            });


            SensorManager sm = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
            Sensor rot = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sm.registerListener(this, rot, SensorManager.SENSOR_DELAY_GAME);
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void run() {
            try {
                while (running) {
                    System.out.println("game thread!");
                    if (!holder.getSurface().isValid())
                        continue;
                    Canvas canvas = holder.lockCanvas();
                    boundLeft = canvas.getClipBounds().left;
                    boundRight = canvas.getClipBounds().right;
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("Hello World!", (canvas.getWidth() / 2), (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)), paint);
                    paint.setTextSize(80);
                    System.out.println(step + " " + a);
                    if (ballX >= screenw / 2 - ball.getWidth() / 2 || ballX <= -1 * screenw / 2 + ball.getWidth() / 2) {
                        step *= -1;
                        System.out.println("shift");
                    }
                    ballX += step;
                    canvas.drawBitmap(ball, (screenw / 2) - ball.getWidth() / 2 + ballX, (screenh / 2) - ball.getHeight(), null);

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