package com.example.gpsappa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.gpsappa.databinding.FragmentDialogBinding;

public class WaypointFragment extends DialogFragment implements View.OnClickListener {
    private FragmentDialogBinding binding;
    String prompt;

    public WaypointFragment(String prompt){
        this.prompt = prompt;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.zipdisplay.setText(prompt);
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
            if(!binding.enterzip.getText().toString().equals("")) {
                Bundle res = new Bundle();
                res.putString("name", binding.enterzip.getText().toString());
                if(prompt.equals("Waypoint Name:"))
                    getParentFragmentManager().setFragmentResult("requestKey", res);
                else
                    getParentFragmentManager().setFragmentResult("requestOther", res);
                dismiss();
            }
        } catch(NumberFormatException e){
            binding.zipdisplay.setText("Try again:");
        }
    }
}
