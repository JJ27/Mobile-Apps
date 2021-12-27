package com.example.listviewprojectb;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

public class Senator implements Parcelable {
    int classnum;
    String name;
    String state;
    String party;
    String lastElection;

    public Senator(String name, int classnum, String state, String party, String lastElection){
        this.classnum = classnum;
        this.name = name;
        this.state = state;
        this.party = party;
        this.lastElection = lastElection;
    }

    public int getClassNum() {
        return classnum;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getParty() {
        return party;
    }

    public String getLastElection() {
        return lastElection;
    }

    public void setLastElection(String lastElection) {
        this.lastElection = lastElection;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Senator(Parcel in) {
        this.classnum = in.readInt();
        String[] s = in.createStringArray();

        this.name = s[0];
        this.state = s[1];
        this.party = s[2];
        this.lastElection = s[3];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(classnum);
        dest.writeStringArray(new String[]{name, state, party, lastElection});
    }

    public static final Parcelable.Creator<Senator> CREATOR = new Parcelable.Creator<Senator>() {
        @Override
        public Senator createFromParcel(Parcel in) {
            return new Senator(in);
        }
        @Override
        public Senator[] newArray(int size) {
            return new Senator[size];
        }
    };
}
