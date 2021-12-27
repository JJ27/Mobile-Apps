package com.example.listviewprojectb;
/* NOTE TO SELF: TO FIX UNKNOWN HOSTNAME
1) Quit emulator app
2) Re-run emulator
 */
import static com.example.listviewprojectb.CustomAdapter.totald;
import static com.example.listviewprojectb.CustomAdapter.totalr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Adapter;
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
    ArrayList<Senator> currdisplay;
    Senator curr;

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
        if(savedInstanceState == null){
            ContentInternet task = new ContentInternet();
            task.execute();
            try{Thread.sleep(17000);} catch(InterruptedException e){Log.d("InterruptedExc",e.toString());}
            currdisplay = senators;
        }
        Log.d("Done","done");
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.composition.setText(totald + "-" + totalr);

            CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, senators, binding, totald, totalr, this);
            binding.listView.setAdapter(adapter);

            binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    curr = (Senator) adapterView.getItemAtPosition(i);
                    binding.electionyear.setText("Next Election: " + curr.getClassNum());
                    binding.state.setText(curr.getState());
                }
            });
        } else{

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
                    ArrayList<Senator> newsen = new ArrayList<Senator>();
                    newsen.addAll(senators);
                    CustomAdapter adapter;
                    switch(parent.getAdapter().getItem(position).toString()){
                        case "Default":
                            newsen = new ArrayList<Senator>(senators);
                            break;
                        case "GOP":
                            newsen.removeIf((Senator s) -> !(s.getParty().charAt(0) == 'R'));
                            break;
                        case "Dem":
                            newsen.removeIf((Senator s) -> (s.getParty().charAt(0) == 'R'));
                            break;
                        case "2022":
                            newsen.removeIf((Senator s) -> (s.getClassNum() == 2024));
                            newsen.removeIf((Senator s) -> (s.getClassNum() == 2026));
                            break;
                        case "2024":
                            newsen.removeIf((Senator s) -> (s.getClassNum() == 2022));
                            newsen.removeIf((Senator s) -> (s.getClassNum() == 2026));
                            break;
                        case "2026":
                            newsen.removeIf((Senator s) -> (s.getClassNum() == 2024));
                            newsen.removeIf((Senator s) -> (s.getClassNum() == 2022));
                            break;
                        case "Solid":
                            newsen.removeIf((Senator s) -> (s.getLastElection().charAt(0) == 'A'));
                            newsen.removeIf((Senator s) -> (Double.parseDouble(s.getLastElection().substring(0,4)) < 65.0));
                            break;
                        case "Likely":
                            newsen.removeIf((Senator s) -> (s.getLastElection().charAt(0) == 'A'));
                            newsen.removeIf((Senator s) -> (Double.parseDouble(s.getLastElection().substring(0,4)) >= 65.0));
                            newsen.removeIf((Senator s) -> (Double.parseDouble(s.getLastElection().substring(0,4)) <= 55.0));
                            break;
                        case "Lean":
                            newsen.removeIf((Senator s) -> (s.getLastElection().charAt(0) == 'A'));
                            newsen.removeIf((Senator s) -> (Double.parseDouble(s.getLastElection().substring(0,4)) >= 55.0));
                            newsen.removeIf((Senator s) -> (Double.parseDouble(s.getLastElection().substring(0,4)) <= 51.0));
                            break;
                        case "Tilt":
                            newsen.removeIf((Senator s) -> (s.getLastElection().charAt(0) == 'A'));
                            newsen.removeIf((Senator s) -> (Double.parseDouble(s.getLastElection().substring(0,4)) > 51.0));
                            break;
                    }
                    adapter = new CustomAdapter(MainActivity.this, R.layout.adapter_layout,newsen, binding, totald, totalr, MainActivity.this);
                    currdisplay = newsen;
                    //adapter.notifyDataSetChanged();
                    binding.llistView.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            binding.llistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    curr = (Senator) binding.llistView.getItemAtPosition(i);
                    binding.lelectionyear.setText("Next Election: " + curr.getClassNum());
                    binding.lstate.setText(curr.getState());
                    binding.lastelec.setText(curr.getLastElection());
                    binding.opinion.setText((curr.getParty().charAt(0) == 'R') ? "Republicans are currently winning the generic ballot. After wins in Virginia and close in NJ, they have high momentum going into 2022." : "After the infrastructure bill, Democrats aim to pass an ambitious Build Back Better bill before the elections. However, Manchin's no-vote may kill their chances.");
                }
            });
        }
    }
    private class ContentInternet extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
            return null;
        }

        @Override
        protected void onPostExecute(Void dVoid) {
            super.onPostExecute(dVoid);
        }
    }
    public String getElectionResult(@NonNull Senator senator){
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
    public void permanentRemove(Senator s, CustomAdapter adapter){
        senators.remove(s);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            outState.putString("state", binding.state.getText().toString());
            outState.putString("election", binding.electionyear.getText().toString());
            outState.putInt("totald", totald);
            outState.putInt("totalr", totalr);
        } else{
            outState.putString("state", binding.lstate.getText().toString());
            outState.putString("election", binding.lelectionyear.getText().toString());
            outState.putInt("totald", totald);
            outState.putInt("totalr", totalr);
            outState.putInt("filter1", binding.filter.getSelectedItemPosition());
            outState.putInt("filter2", binding.filter2.getSelectedItemPosition());
        }
        outState.putParcelableArrayList("display",currdisplay);
        outState.putParcelableArrayList("senators", senators);
        outState.putParcelable("curr", curr);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        senators = savedInstanceState.getParcelableArrayList("senators");
        totalr = savedInstanceState.getInt("totalr");
        totald = savedInstanceState.getInt("totald");
        currdisplay = savedInstanceState.getParcelableArrayList("display");
        curr = savedInstanceState.getParcelable("curr");
        CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, currdisplay, binding, totald, totalr, this);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            binding.listView.setAdapter(adapter);
            binding.composition.setText(totald+"-"+totalr);
            binding.state.setText(savedInstanceState.getString("state"));
            binding.electionyear.setText(savedInstanceState.getString("election"));
        } else{
            binding.llistView.setAdapter(adapter);
            binding.lcomposition.setText(totald+"-"+totalr);
            binding.lstate.setText(savedInstanceState.getString("state"));
            binding.lelectionyear.setText(savedInstanceState.getString("election"));
            Log.d("filter1", savedInstanceState.getInt("filter")+"");
            binding.filter.setSelection(savedInstanceState.getInt("filter"));
            binding.filter2.setSelection(savedInstanceState.getInt("filter2"));
            if(curr != null) {
                binding.lastelec.setText(curr.getLastElection());
                binding.opinion.setText((curr.getParty().charAt(0) == 'R') ? "Republicans are currently winning the generic ballot. After wins in Virginia and close in NJ, they have high momentum going into 2022." : "After the infrastructure bill, Democrats aim to pass an ambitious Build Back Better bill before the elections. However, Manchin's no-vote may kill their chances.");
            }
        }
    }
}