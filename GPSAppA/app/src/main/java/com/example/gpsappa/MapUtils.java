package com.example.gpsappa;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapUtils {
    public static List<LatLng> readPolyline(String encoded) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.addAll(PolyUtil.decode(encoded.trim().replace("\\\\", "\\")));
        return latLngs;
    }
}
