package com.example.wifimanagerprac;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    WifiManager wm;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        Runnable wifiUse = new Runnable() {
            @Override
            public void run() {
                List<ScanResult> ls = wm.getScanResults();
                List<String> ssids = new ArrayList<String>();
                for(ScanResult l : ls){
                    ssids.add(l.SSID);
                }
                ListView lw = findViewById(R.id.listview);
                ArrayAdapter<String> ad = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, ssids);
                lw.setAdapter(ad);
            }
        };

        wm.registerScanResultsCallback(new Executor() {
            @Override
            public void execute(Runnable runnable) {
                wm.startScan();
            }
        }, new WifiManager.ScanResultsCallback() {
            @Override
            public void onScanResultsAvailable() {
                wifiUse.run();
            }
        });
        wm.startScan();
        List<ScanResult> ls = wm.getScanResults();
        List<String> ssids = new ArrayList<String>();
        for(ScanResult l : ls){
            System.out.println(l.SSID);
            ssids.add(l.SSID);
        }
        ListView lw = findViewById(R.id.listview);
        ArrayAdapter<String> ad = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, ssids);
        lw.setAdapter(ad);
    }
}