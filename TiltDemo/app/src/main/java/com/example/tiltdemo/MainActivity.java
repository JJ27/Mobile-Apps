package com.example.tiltdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    float brightnessLev = 0;
    WindowManager.LayoutParams brightnessPar;
    TextView x,y,z,b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        x = findViewById(R.id.xaxis);
        y = findViewById(R.id.yaxis);
        z = findViewById(R.id.zaxis);
        b = findViewById(R.id.brightness);

        brightnessPar = getWindow().getAttributes();
        b.setText(brightnessLev+"");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        x.setText(""+ sensorEvent.values[0]);
        y.setText(""+ sensorEvent.values[1]);
        z.setText(""+ sensorEvent.values[2]);

        if(brightnessLev < 1.0f)
            brightnessLev += 0.01f;
        brightnessPar.screenBrightness = brightnessLev;
        getWindow().setAttributes(brightnessPar);
        b.setText(brightnessLev+"");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}