package com.example.listviewprojectb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.listviewprojectb.databinding.ActivityMainBinding;
import com.example.listviewprojectb.databinding.AdapterLayoutBinding;
import com.example.listviewprojectb.databinding.AdapterLayoutBindingImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<Senator> senators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        senators = new ArrayList<Senator>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_current_United_States_senators").get();
            ArrayList<String> senNames = new ArrayList<String>();
            ArrayList<Integer> electionyears = new ArrayList<Integer>();
            Element table = doc.getElementById("senators");
            Elements rows = table.select("tr");
            ArrayList<String> party = new ArrayList<String>();
            for(int i = 1; i < rows.size(); i++){
                if(i % 2 == 1) {
                    Log.d("Senator", rows.get(i).select("th").get(0).text());
                    senNames.add(rows.get(i).select("th").get(0).text());
                    party.add(rows.get(i).select("td").get(3).text());
                    electionyears.add(Integer.parseInt(rows.get(i).select("td").get(9).text()));
                } else{
                    Log.d("Senator", rows.get(i).select("th").get(0).text());
                    senNames.add(rows.get(i).select("th").get(0).text());
                    party.add(rows.get(i).select("td").get(2).text());
                    electionyears.add(Integer.parseInt(rows.get(i).select("td").get(8).text()));
                }
            }
            for(int i = 0; i < 100; i++){
                senators.add(new Senator(senNames.get(i),electionyears.get(i),"NJ",party.get(i),"51-49"));
            }
        } catch (IOException e) {Log.d("IOException", e.toString());}

        CustomAdapter adapter = new CustomAdapter(this,R.layout.adapter_layout, senators);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                binding.electionyear.setText("Hello");
                //binding.textView.setText(adapterView.getAdapter().getItem(i).getClass().toString());
            }
        });
    }

    public class CustomAdapter extends ArrayAdapter<Senator> {
        List<Senator> list;
        Context context;
        int xmlResource;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Senator> objects){
            super(context, resource, objects);
            xmlResource = resource;
            list = objects;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterLayout = layoutInflater.inflate(xmlResource,null);
            AdapterLayoutBindingImpl bindings = DataBindingUtil.inflate(layoutInflater.from(context), R.layout.adapter_layout, parent, false);
            bindings.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

            bindings.name.setText(list.get(position).getName());
            bindings.party.setImageResource((list.get(position).getParty().charAt(0) == 'R') ? R.drawable.republican : R.drawable.democrat);
            return bindings.getRoot();
        }

        public List<Senator> getList() {
            return list;
        }
    }
}