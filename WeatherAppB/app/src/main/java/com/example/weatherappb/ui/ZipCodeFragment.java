package com.example.weatherappb.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherappb.R;
import com.example.weatherappb.databinding.FragmentDashboardBinding;
import com.example.weatherappb.databinding.FragmentDialogBinding;
import com.example.weatherappb.ui.dashboard.DashboardViewModel;

public class ZipCodeFragment extends DialogFragment implements View.OnClickListener {
    private FragmentDialogBinding binding;
    private ZipViewModel zipview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        zipview = new ViewModelProvider(this).get(ZipViewModel.class);
        binding.submit.setOnClickListener(this);
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        try {
            zipview.setZipCode(binding.enterzip.getText().toString());
            if(!binding.enterzip.getText().toString().equals("")) {
                Bundle res = new Bundle();
                res.putString("zipcode", binding.enterzip.getText().toString());
                getParentFragmentManager().setFragmentResult("requestKey", res);
                dismiss();
            }
        } catch(NumberFormatException e){
            binding.zipdisplay.setText("Try again:");
        }
    }
}
