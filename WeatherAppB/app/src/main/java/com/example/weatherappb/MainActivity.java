package com.example.weatherappb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.weatherappb.ui.ZipCodeFragment;
import com.example.weatherappb.ui.dashboard.DashboardFragment;
import com.example.weatherappb.ui.home.HomeFragment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weatherappb.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        DialogFragment zc = new ZipCodeFragment();
        zc.show(getSupportFragmentManager(),"Hello");
        getSupportFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String zipcode = result.getString("zipcode");
                Intent apiIntent = new Intent(MainActivity.this, APICall.class);
                apiIntent.putExtra("zipcode", zipcode);
                startService(apiIntent);
            }
        });

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setBackgroundColor(getColor(android.R.color.transparent));
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_home)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(binding.navView, navController);
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_dashboard, new DashboardFragment()).commit();
        IntentFilter filter = new IntentFilter("Received");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        BroadcastReceiver receiver = new APIReceiver();
        registerReceiver(receiver, filter);
    }

    public class APIReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ObjectMapper mapper = new ObjectMapper();
            DashboardFragment weather = (DashboardFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_dashboard);
            try {
                JsonNode root = mapper.readTree(intent.getStringExtra("json"));
                System.out.println(intent.getStringExtra("json"));
                if(root.get("current").get("temp").toString().length() == 5)
                    binding.currtemp.setText(root.get("current").get("temp").toString());
                else
                    binding.currtemp.setText(root.get("current").get("temp").toString() + "0");
                binding.currtemp.setText(root.get("current").get("temp").toString());
                JsonNode root2 = mapper.readTree(root.get("current").get("weather").toString().substring(1).replaceAll("]",""));
                binding.weathername.setText(root2.get("main").toString().replaceAll("\"",""));
                Picasso.get().load("http://openweathermap.org/img/wn/"+root2.get("icon").toString().replaceAll("\"","")+"@2x.png").into(binding.curricon);
                getSupportActionBar().setTitle(intent.getStringExtra("city").replaceAll("\"","") + "(" + intent.getStringExtra("lat") + "," + intent.getStringExtra("lon")+")");
                ArrayNode array = (ArrayNode) root.get("hourly");
                ArrayList<Weather> weatherlist = new ArrayList<Weather>();
                for(int i = 0; i < 4; i++){
                    Date date = new java.util.Date(Long.parseLong(array.get(i).get("dt").toString())*1000L);
                    SimpleDateFormat df = new java.text.SimpleDateFormat("hh:mm a");
                    df.setTimeZone(java.util.TimeZone.getTimeZone("GMT-5"));
                    String formattedDate = df.format(date);
                    JsonNode root4 = mapper.readTree(array.get(i).get("weather").toString().substring(1).replaceAll("]",""));
                    weatherlist.add(new Weather(formattedDate, array.get(i).get("temp").toString(), root4.get("main").toString().replaceAll("\"",""), root4.get("icon").toString().replaceAll("\"","")));
                }
                CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.adapter_layout, weatherlist);
                binding.listview.setAdapter(adapter);
            } catch (Exception e) {
                binding.currtemp.setText("Invalid");
                e.printStackTrace();
            }

        }
    }

}