package com.example.weatherappb;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.weatherappb.databinding.AdapterLayoutBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Weather> {
    List<Weather> list;
    Context context;
    int xmlResource;
    AdapterLayoutBinding bindings;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Weather> objects){
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
        bindings = DataBindingUtil.inflate(layoutInflater.from(context), R.layout.adapter_layout, parent, false);
        bindings.time.setText(list.get(position).getTime());
        if(list.get(position).getTemp().length() == 5)
            bindings.temp.setText(list.get(position).getTemp());
        else
            bindings.temp.setText(list.get(position).getTemp() + "0");
        bindings.conditions.setText(list.get(position).getDesc());
        Picasso.get().load("http://openweathermap.org/img/wn/"+list.get(position).getIcon()+"@2x.png").into(bindings.weathericon);
        return bindings.getRoot();
    }
    public List<Weather> getList() {
        return list;
    }
}
