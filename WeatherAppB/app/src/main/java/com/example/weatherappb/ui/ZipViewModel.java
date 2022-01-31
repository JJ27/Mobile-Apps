package com.example.weatherappb.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ZipViewModel extends ViewModel {

    private MutableLiveData<String> zipCode;

    public ZipViewModel() {
        zipCode = new MutableLiveData<>();
    }
    public void setZipCode(String zip){
        zipCode.setValue(zip);
    }
    public LiveData<String> getZipCode() {
        return zipCode;
    }
}