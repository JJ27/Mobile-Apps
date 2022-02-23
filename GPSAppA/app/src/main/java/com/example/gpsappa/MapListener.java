package com.example.gpsappa;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapListener implements ViewTreeObserver.OnGlobalLayoutListener, OnMapReadyCallback {
    public interface CombinedListener{
        public void onMapReady(GoogleMap googleMap);
    }
    private final SupportMapFragment mapFragment;
    private final View mapView;
    private final CombinedListener callback;
    private boolean isViewReady;
    private boolean isMapReady;
    private GoogleMap googleMap;

    public MapListener(SupportMapFragment mapFrag, CombinedListener callback){
        this.mapFragment = mapFrag;
        mapView = this.mapFragment.getView();
        this.callback = callback;
        isViewReady = false;
        isMapReady = false;
        googleMap = null;

        enableListener();
    }

    private void enableListener() {
        if(mapView.getWidth() > 0 && mapView.getHeight() > 0)
            isViewReady = true;
        else
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onGlobalLayout() {
        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        isViewReady = true;
        startCallback();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        isMapReady = true;
        startCallback();
    }

    private void startCallback() {
        callback.onMapReady(googleMap);
    }
}
