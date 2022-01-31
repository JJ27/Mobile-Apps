package com.example.weatherappb;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

public class Weather {
    String time, temp, desc, icon;

    public Weather(String time, String temp, String desc, String icon){
        this.time = time;
        this.desc = desc;
        this.icon = icon;
        this.temp = temp;
    }

    public String getDesc() {
        return desc;
    }

    public String getTemp() {
        return temp;
    }

    public String getIcon() {
        return icon;
    }

    public String getTime() {
        return time;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
