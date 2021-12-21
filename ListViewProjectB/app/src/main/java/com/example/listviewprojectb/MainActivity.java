package com.example.listviewprojectb;
/* NOTE TO SELF: TO FIX UNKNOWN HOSTNAME
1) Quit emulator app
2) Re-run emulator
 */
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.listviewprojectb.databinding.ActivityMainBinding;
import com.example.listviewprojectb.databinding.AdapterLayoutBinding;
import com.example.listviewprojectb.databinding.AdapterLayoutBindingImpl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<Senator> senators;
    int totald, totalr;

    @Override
    protected void onStart() {
        super.onStart();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            binding.listView.setFriction((float)(ViewConfiguration.getScrollFriction() * 1.5));
        else
            binding.llistView.setFriction((float)(ViewConfiguration.getScrollFriction() * 1.2));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        senators = new ArrayList<Senator>();
        if(savedInstanceState == null)
            initialize();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.composition.setText(totald + "-" + totalr);

            CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, senators, binding, totald, totalr);
            binding.listView.setAdapter(adapter);

            binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    binding.electionyear.setText("Next Election: " + senators.get(i).getClassNum());
                    binding.state.setText(senators.get(i).getState());
                }
            });
        } else{
            CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, senators, binding, totald, totalr);
            binding.llistView.setAdapter(adapter);
            setSpinner(binding.filter, new ArrayList<String>(Arrays.asList("Default","Party","Class","Strength")));
            setSpinner(binding.filter2, new ArrayList<String>(Arrays.asList("Default")));

            binding.filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch(parent.getAdapter().getItem(position).toString()){
                        case "Default":
                            setSpinner(binding.filter2, new ArrayList<String>(Arrays.asList("Default")));
                            break;
                        case "Party":
                            setSpinner(binding.filter2, new ArrayList<String>(Arrays.asList("Dem", "GOP")));
                            break;
                        case "Class":
                            setSpinner(binding.filter2, new ArrayList<String>(Arrays.asList("2022", "2024", "2026")));
                            break;
                        case "Strength":
                            setSpinner(binding.filter2, new ArrayList<String>(Arrays.asList("Solid", "Likely", "Lean", "Tilt")));
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            binding.filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<Senator> newsen = new ArrayList<Senator>(Collections.copy(senators));
                    switch(parent.getAdapter().getItem(position).toString()){
                        case "Default":
                            CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.adapter_layout, senators, binding, totald, totalr);
                            binding.llistView.setAdapter(adapter);
                            break;
                        case "GOP":
                            for(Senator s: newsen){
                                if(!(s.getParty().charAt(0) == 'R'))
                                    newsen.remove(s);
                            }
                            CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.adapter_layout,newsen, binding, totald, totalr);
                            binding.llistView.setAdapter(adapter);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            binding.llistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    binding.lelectionyear.setText("Next Election: " + senators.get(i).getClassNum());
                    binding.lstate.setText(senators.get(i).getState());
                    binding.lastelec.setText(senators.get(i).getLastElection());
                    binding.opinion.setText((senators.get(i).getParty().charAt(0) == 'R') ? "Republicans are currently winning the generic ballot. After wins in Virginia and close in NJ, they have high momentum going into 2022." : "After the infrastructure bill, Democrats aim to pass an ambitious Build Back Better bill before the elections. However, Manchin's no-vote may kill their chances.");
                }
            });
        }
    }

    public void initialize(){
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_current_United_States_senators").get();
            ArrayList<String> senNames = new ArrayList<String>();
            ArrayList<Integer> electionyears = new ArrayList<Integer>();
            ArrayList<String> state = new ArrayList<String>();
            Element table = doc.getElementById("senators");
            Elements rows = table.select("tr");
            ArrayList<String> party = new ArrayList<String>();
            for(int i = 1; i < rows.size(); i++){
                senNames.add(rows.get(i).select("th").get(0).text());
                if(i % 2 == 1) {
                    party.add(rows.get(i).select("td").get(3).text());
                    electionyears.add(Integer.parseInt(rows.get(i).select("td").get(9).text()));
                    state.add(rows.get(i).select("td").get(0).text());
                } else{
                    party.add(rows.get(i).select("td").get(2).text());
                    electionyears.add(Integer.parseInt(rows.get(i).select("td").get(8).text()));
                    state.add(rows.get(i-1).select("td").get(0).text());
                }
                senators.add(new Senator(senNames.get(i-1).trim(),electionyears.get(i-1),state.get(i-1),party.get(i-1),"51-49"));
                senators.get(i-1).setLastElection(getElectionResult(senators.get(i-1)));
                Log.d("Elec",senators.get(i-1).getLastElection());
                if ((party.get(i-1).contains("Republican")))
                    totalr++;
                else
                    totald++;
            }
        } catch (IOException e) {Log.d("IOException", e.toString());}
    }
    public String getElectionResult(Senator senator){
        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/" + senator.getClassNum() + "_United_States_Senate_elections").get();
            switch (senator.getClassNum()) {
                case 2022:
                    Elements tablerow = doc.getElementsByClass("wikitable").get(4).select("tr:contains(" + senator.getName().split(" ")[0]+" "+senator.getName().split(" ")[1] + ")");
                    return (tablerow.select("td").get(2).text().charAt(0) != 'A') ? tablerow.select("td").get(2).text().substring(0,5): tablerow.select("td").get(2).text().substring(0,9);
                case 2024:
                    tablerow = doc.getElementsByClass("wikitable").get(3).select("tr:contains(" + senator.getName().split(" ")[0]+" "+senator.getName().split(" ")[1] + ")");
                    return tablerow.select("td").get(3).text().substring(0,5);
                case 2026:
                    tablerow = doc.getElementsByClass("wikitable").get(2).select("tr:contains(" + senator.getName().split(" ")[0]+" "+senator.getName().split(" ")[1] + ")");
                    return tablerow.select("td").get(3).text().substring(0,5);
            }
        } catch (IOException e) {Log.d("IOException", e.toString());}
        return "Hello!";
    }
    public void setSpinner(Spinner spin, ArrayList<String> strings){
        ArrayAdapter<String> selectorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        selectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(selectorAdapter);
    }
}