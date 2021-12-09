package com.example.listviewdemo2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.listviewdemo2.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<Reindeer> reindeerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        reindeerList = new ArrayList<Reindeer>(Arrays.asList(new Reindeer(android.R.drawable.btn_star_big_on,"Comet"),new Reindeer(android.R.drawable.btn_star_big_on,"Rudolph")));

        CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, reindeerList);
        binding.list.setAdapter(adapter);
    }

    public class CustomAdapter extends ArrayAdapter<Reindeer> {
        List<Reindeer> list;
        Context context;
        int xmlResource;
        //ActivityMainBinding binding2 = DataBindingUtil.setContentView(MainActivity.CustomAdapter,R.layout.adapter_layout);

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Reindeer> objects) {
            super(context, resource, objects);
            xmlResource = resource;
            list = objects;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterLayout = layoutInflater.inflate(xmlResource, null);

            TextView name = adapterLayout.findViewById(R.id.textView);
            ImageView image = adapterLayout.findViewById(R.id.imageView);
            Button remove = adapterLayout.findViewById(R.id.button);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

            name.setText(list.get(position)+" "+position);
            image.setImageResource(android.R.drawable.btn_star_big_on);
            return adapterLayout;
        }
    }
}