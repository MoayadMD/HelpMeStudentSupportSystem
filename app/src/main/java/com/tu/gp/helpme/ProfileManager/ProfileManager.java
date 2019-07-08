package com.tu.gp.helpme.ProfileManager;

import android.util.Log;

import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.FeedBackManager.FeedBack;

import java.util.List;

public class ProfileManager
{

    public static String createProfile
            (String password,String email,String type, String fullName, String phoneNumber,List<Spec> specs)
    {
        Profile profile = new Profile(password,email,fullName,phoneNumber,Boolean.parseBoolean(type),specs);
        String result = DataManager.addNewProfileToDatabase(profile);
        int profileID = DataManager.getLatestValueInColumn("profile","profileID");
        if(profileID>0)
        {
            profile.setProfileID(profileID);
            for(Spec spec:specs)
            {
                DataManager.addSpecToProfile(profile.getProfileID(),spec.getSpecID());
            }
        }else
        {
            result+="Invalid Profile ID";
        }
        return result;
    }

    public static Profile getProfileByID(int profileID)
    {

        Profile profile = DataManager.getProfileByID(profileID);
        return profile;
    }
    public static String createSpec(String specsName)
    {
        return DataManager.addNewSpecToDatabase(specsName);
    }
    public static void addSepcToProfile(int profileID,int...specsIDs)
    {
        for(int specID : specsIDs)
        {
            DataManager.addSpecToProfile(profileID,specID);
        }
    }
    public static String modifyProfile(String fullName,String password,String email,String phoneNumber,boolean type,List<Spec> mySpecs,int profileID)
    {
        return DataManager.modifyProfile(fullName,password,email,phoneNumber,type,mySpecs,profileID);
    }

    public static List<Spec> getProfileSpec(int profileID)
    {
        return DataManager.getProfileSpecs(profileID);
    }

    public static List<Spec> getAllSpecs()
    {
        return DataManager.getAllSpecs();
    }
}
