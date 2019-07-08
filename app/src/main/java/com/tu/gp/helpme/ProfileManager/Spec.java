package com.tu.gp.helpme.ProfileManager;

public class Spec
{
    private int specID;
    private String specName;

    public Spec(String specName) {
        this.specName = specName;
    }

    public int getSpecID() {
        return specID;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecID(int specID) {
        this.specID = specID;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    @Override
    public String toString() {
        return specName;
    }
}
