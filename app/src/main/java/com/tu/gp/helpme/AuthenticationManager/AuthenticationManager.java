package com.tu.gp.helpme.AuthenticationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.R;

public  class  AuthenticationManager
{
    public static int profileID;
    public static boolean login(String email, String password, SharedPreferences.Editor editor, Context context)
    {
        int pID =DataManager.authenticate(email, password);
        if(pID>0)
        {
            Log.d("Login","Successfuly Login");
            editor.putString(context.getString(R.string.email),email);
            editor.apply();
            editor.putString(context.getString(R.string.password),password);
            editor.apply();
            profileID = pID;
            Log.d("P ID= ",profileID+" Is My ID");
            return true;
        }else
        {
            Log.d("Login","Failed Logingin");
            return false;
        }
    }
    public static void chickSharedPrefrences(SharedPreferences preferences, SharedPreferences.Editor editor, Context context)
    {
        String username = preferences.getString(context.getString(R.string.email),"");
        String password = preferences.getString(context.getString(R.string.password),"");
    }

}
