package com.example.listviewproject;

public class Senator {
    int classnum;
    String name;
    String state;
    String party;

    public Senator(int classnum, String name, String state, String party){
        this.classnum = classnum;
        this.name = name;
        this.state = state;
        this.party = party;
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
}
