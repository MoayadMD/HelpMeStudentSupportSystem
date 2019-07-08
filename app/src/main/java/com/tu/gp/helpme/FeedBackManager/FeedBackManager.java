package com.tu.gp.helpme.FeedBackManager;

import android.util.Log;

import com.tu.gp.helpme.DataManager.DataManager;

public class FeedBackManager {
    public static String writeNewFeedBack(int requestID, int writerID, int recipientID, int rate, String notes)
    {
        FeedBack feedBack = new FeedBack(requestID,notes,writerID,recipientID,rate);
        Log.d("profileFBID",feedBack.getRequestID()+"");
        String result = DataManager.addNewFeedBack(feedBack);
        result += " "+DataManager.updateProfileRating(recipientID,feedBack.rate);
        return result;
    }
}
