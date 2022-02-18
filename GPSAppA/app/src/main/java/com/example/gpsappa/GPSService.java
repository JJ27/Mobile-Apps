package com.example.gpsappa;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.media.VolumeShaper;
import android.os.Build;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gpsappa.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GPSService extends AppCompatActivity {
    private final Context context;
    ActivityMainBinding binding;
    public static FusedLocationProviderClient fuse;
    public static LocationCallback callback;
    public static LocationRequest locationRequest;
    String current;
    String recent;
    String fav;
    List<String> prevAddy;
    List<Stopwatch> times;
    List<Location> prevLoc;
    ArrayList<String> recents;
    ArrayList<Stopwatch> recenttimes;
    float totaldist;

    public float getTotaldist() {
        return totaldist;
    }
    public String getCurrent() {
        return current;
    }
    public String getFav() {
        return fav;
    }

    public String getRecent() {
        return recent;
    }

    @SuppressLint("MissingPermission")
    public GPSService(Context context, int type, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
        this.totaldist = 0f;
        recents = new ArrayList<String>();
        recenttimes = new ArrayList<Stopwatch>();
        fav = null;
        fuse = LocationServices.getFusedLocationProviderClient(context);
        fuse.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUI(location);
                }
            });
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUI(locationResult.getLastLocation());
            }
        };
        locationRequest = LocationRequest.create().setInterval(1000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(1);
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    public GPSService(Context context, int type, ActivityMainBinding binding, float dist, String current, String fav, String rec) {
        this.context = context;
        this.binding = binding;
        this.totaldist = dist;
        this.current = current;
        this.fav = fav;
        this.recent = rec;
        fuse = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Denied");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else{
            fuse.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUI(location);
                }
            });
        }
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUI(locationResult.getLastLocation());
            }
        };
        locationRequest = LocationRequest.create().setInterval(1000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(1);
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    public void updateUI(Location loc){
        binding.lat.setText("Latitude: " + loc.getLatitude());
        binding.lon.setText("Longitude: " + loc.getLongitude());
        //TODO: Include speed using loc.hasSpeed() and loc.getSpeed()
        Geocoder gc = new Geocoder(context);
        GPSApplication app = (GPSApplication) context.getApplicationContext();
        times = app.getTimes();
        prevLoc = app.getLocations();
        prevAddy = app.getAddresses();
        Runnable count = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.currtime.setText("Time: " + times.get(prevAddy.indexOf(current)).elapsed(TimeUnit.SECONDS) + " s");
                            }
                        });
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(count).start();
        try {
            try{times.get(prevAddy.indexOf(current)).stop();} catch(ArrayIndexOutOfBoundsException | IllegalStateException e){e.printStackTrace();}
            if(fav == null)
                fav = current;
            else
                try{if(times.get(prevAddy.indexOf(current)).elapsed(TimeUnit.MILLISECONDS) > times.get(prevAddy.indexOf(fav)).elapsed(TimeUnit.MILLISECONDS))
                    fav = current;} catch(ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
            try{
            if(!current.equals(gc.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1).get(0).getAddressLine(0))){
                recent = current;
                System.out.println("if");
                current = gc.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1).get(0).getAddressLine(0);
                if(recent != null && !recent.equals(current)) {
                    System.out.println("inner");
                    System.out.println("Recent: " + recent);
                    recents.add(0, recent);
                    if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                        binding.recent.setText("Recent: " + recent);
                    try {
                        recenttimes.add(0, times.get(prevAddy.indexOf(recent)));
                        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                            binding.recenttime.setText("Recent Time: " + times.get(prevAddy.indexOf(recent)).elapsed(TimeUnit.SECONDS) + " s");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            } else{
                System.out.println("else");
                if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    binding.recent.setText("Recent: " + recent);
                System.out.println("Recent: " + recent);
                if(recent != null)
                    recents.add(0, recent);
                try {
                    if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                        binding.recenttime.setText("Recent Time: " + times.get(prevAddy.indexOf(recent)).elapsed(TimeUnit.SECONDS) + " s");
                    recenttimes.add(0, times.get(prevAddy.indexOf(recent)));
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }} catch (NullPointerException e){e.printStackTrace();}
            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                RecyclerView recyclerView = binding.recycle;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                CustomAdapter adapter = new CustomAdapter(recents, recenttimes);
                recyclerView.setAdapter(adapter);
            }
            binding.fav.setText("Favorite: " + fav);
            try{binding.favtime.setText("Fav Time: " + times.get(prevAddy.indexOf(fav)).elapsed(TimeUnit.SECONDS) + " s");} catch (ArrayIndexOutOfBoundsException e){e.printStackTrace();}
            current = gc.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1).get(0).getAddressLine(0);
            if(prevAddy.contains(current)){
                if(!times.get(prevAddy.indexOf(current)).isRunning())
                    times.get(prevAddy.indexOf(current)).start();
            } else{
                prevAddy.add(current);
                times.add(Stopwatch.createStarted(
                        new Ticker() {
                            public long read() {
                                return android.os.SystemClock.elapsedRealtimeNanos();
                            }}));
            }
            binding.address.setText(current);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(!Build.FINGERPRINT.contains("generic")) {
                if (loc.distanceTo(prevLoc.get(prevLoc.size() - 1)) <= 500) {
                    System.out.println(loc.distanceTo(prevLoc.get(prevLoc.size() - 1)));
                    totaldist += loc.distanceTo(prevLoc.get(prevLoc.size() - 1));
                }
            } else{
                System.out.println(loc.distanceTo(prevLoc.get(prevLoc.size() - 1)));
                totaldist += loc.distanceTo(prevLoc.get(prevLoc.size() - 1));
            }
        } catch(ArrayIndexOutOfBoundsException e){}
        prevLoc.add(loc);
        binding.distance.setText("Distance: " + totaldist);
        //TODO: Find if address is new or not
        /*currentAddy = s;
        for(Address q: prevAddy){
            if(q.getAddressLine(0).equals(s.getAddressLine(0)))
        }
        if(!prevAddy.contains(s)){
            System.out.println(s);
            prevAddy.add(s);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fuse.removeLocationUpdates(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fuse.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<String> recents;
        private ArrayList<Stopwatch> times;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView recent;
            private final TextView time;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                recent = (TextView) view.findViewById(R.id.recentview);
                time = (TextView) view.findViewById(R.id.recenttimeview);
            }

            public TextView getRecent() {
                return recent;
            }

            public TextView getTime() {
                return time;
            }
        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public CustomAdapter(ArrayList<String> recents, ArrayList<Stopwatch> times) {
            this.recents = recents;
            this.times = times;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_layout, viewGroup, false);

            return new ViewHolder(view);
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            try {
                viewHolder.getRecent().setText(recents.get(position));
                viewHolder.getTime().setText(times.get(position).elapsed(TimeUnit.SECONDS) + " s");
            } catch(IndexOutOfBoundsException e){e.printStackTrace();}
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if(recents != null)
                return recents.size();
            return 0;
        }
    }


}
