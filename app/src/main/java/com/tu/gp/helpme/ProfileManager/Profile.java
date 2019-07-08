package com.tu.gp.helpme.ProfileManager;

import java.util.List;

public class Profile
{
    private int profileID;
    private String password,email,fullName,phoneNumber;
    private List<Spec> listOfSpecs;
    private boolean type; //true = Student, false = Teacher
    private int rating;

    public Profile(String password, String email, String fullName, String phoneNumber, boolean type,List<Spec> specs) {

        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.listOfSpecs = specs;
    }
    public Profile(String fullName,String email,String phoneNumber,boolean type,int rating)
    {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.rating = rating;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isType() {
        return type;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean addSpec(Spec newSpec)
    {
        return this.listOfSpecs.add(newSpec);
    }
    public int getProfileID() {
        return profileID;
    }
    public String getFullName() {

        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber;
    }

    public List<Spec> getListOfSpecs() {
        return listOfSpecs;
    }

    public void setListOfSpecs(List<Spec> listOfSpecs) {
        this.listOfSpecs = listOfSpecs;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Profile{" +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", listOfSpecs=" + listOfSpecs +
                ", type=" + type + '\'' +
                ", profileID= "+ profileID +
                '}';
    }
}
