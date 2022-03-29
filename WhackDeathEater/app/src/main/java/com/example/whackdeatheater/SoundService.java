package com.example.whackdeatheater;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.Nullable;
public class SoundService extends Service {
    MediaPlayer mP;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mP = MediaPlayer.create(this, R.raw.march);
        mP.setLooping(true); // Set looping
        mP.setVolume(100, 100);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        mP.start();
        return startId;
    }
    @Override
    public void onDestroy() {
        mP.stop();
        mP.release();
    }
    @Override
    public void onLowMemory() {
    }
}