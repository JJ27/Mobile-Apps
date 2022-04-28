package com.example.surfaceviewprac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    GameSurface gs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        gs = new GameSurface(this);
        setContentView(gs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gs.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gs.pause();
    }

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {
        Thread gameThread;
        SurfaceHolder holder = getHolder();
        volatile boolean running = false;
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
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            Display disp = getWindowManager().getDefaultDisplay();
            Point screenSize = new Point();
            disp.getSize(screenSize);

            screenw = screenSize.x;
            screenh = screenSize.y;


            paint = new Paint();

            SensorManager sm = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
            Sensor rot = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sm.registerListener(this, rot, SensorManager.SENSOR_DELAY_GAME);
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            a = sensorEvent.values[2];
            if(ballX == screenw/2 - ball.getWidth()/2 || ballX == -1 * screenw/2 + ball.getWidth()/2)
                step *= -1;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void run() {
            while(running){
                if(!holder.getSurface().isValid())
                    continue;
                Canvas canvas = holder.lockCanvas();
                boundLeft = canvas.getClipBounds().left;
                boundRight = canvas.getClipBounds().right;
                canvas.drawRGB(0,255,0);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Hello World!", (canvas.getWidth() / 2), (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)), paint);
                paint.setTextSize(80);
                step += a;
                System.out.println(step + " " + a);
                if(ballX >= screenw/2 - ball.getWidth()/2 || ballX <= -1 * screenw/2 + ball.getWidth()/2) {
                    step *= -1;
                    System.out.println("shift");
                }
                ballX += step;
                canvas.drawBitmap(ball, (screenw/2)-ball.getWidth()/2+ballX,(screenh/2)-ball.getHeight(),null);



                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume(){
            running = true;
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