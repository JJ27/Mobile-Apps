package com.example.listviewprojectb;

public class Senator {
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
}
