package com.example.listviewprojectb;

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

import com.example.listviewprojectb.databinding.ActivityMainBinding;
import com.example.listviewprojectb.databinding.AdapterLayoutBinding;
import com.example.listviewprojectb.databinding.AdapterLayoutBindingImpl;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Senator> {
    List<Senator> list;
    Context context;
    MainActivity m;
    int xmlResource;
    ActivityMainBinding binding;
    static int totald, totalr;
    AdapterLayoutBindingImpl bindings;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Senator> objects, ActivityMainBinding binding, int totald, int totalr, MainActivity m){
        super(context, resource, objects);
        xmlResource = resource;
        list = objects;
        this.context = context;
        this.binding = binding;
        CustomAdapter.totald = totald;
        CustomAdapter.totalr = totalr;
        this.m = m;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource,null);
        bindings = DataBindingUtil.inflate(layoutInflater.from(context), R.layout.adapter_layout, parent, false);
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)bindings.name.getLayoutParams();
            param.weight = 2.0f;
            bindings.name.setLayoutParams(param);
            param = (LinearLayout.LayoutParams)bindings.party.getLayoutParams();
            param.weight = 2.0f;
            bindings.party.setLayoutParams(param);
            bindings.listitem.setWeightSum(7.0f);
        }
        bindings.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Remove",getItem(position).getName());
                m.permanentRemove(getItem(position), CustomAdapter.this);
                if ((list.remove(position).getParty().contains("Republican"))) {
                    totalr--;
                } else {
                    totald--;
                }
                if(context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT)
                    binding.composition.setText(totald + "-" + totalr);
                else
                    binding.lcomposition.setText(totald + "-" + totalr);
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

    public AdapterLayoutBindingImpl getBinding() {
        return bindings;
    }

    public static int getTotald() {
        return totald;
    }

    public static int getTotalr() {
        return totalr;
    }
}
