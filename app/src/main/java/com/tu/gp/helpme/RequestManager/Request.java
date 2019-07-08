package com.tu.gp.helpme.RequestManager;

import android.annotation.TargetApi;
import android.icu.util.LocaleData;
import android.os.Build;
import android.util.Log;

import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.Spec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Request implements Comparable<Request>
{
   private int requestID,serviceType,requestStatus,serviceRequesterID;
    private Spec requestSpec;
    private String requestDate;
    private String question;
    private Profile serviceRequester;

    public Request(int requestID,int serviceType, int serviceRequesterID, Spec spec, String question, String requestDate,int requestStatus)
    {
        this.serviceType = serviceType;
        this.requestStatus = requestStatus;
        this.requestDate =requestDate;
        this.serviceRequesterID = serviceRequesterID;
        this.requestSpec = spec;
        this.question = question;
        this.requestID = requestID;
    }
    public Request(int serviceType, int serviceRequesterID, Spec spec, String question, String requestDate,int requestStatus)
    {
        this.serviceType = serviceType;
        this.requestStatus = requestStatus;
        this.requestDate =requestDate;
        this.serviceRequesterID = serviceRequesterID;
        this.requestSpec = spec;
        this.question = question;
    }

    public Request(int serviceType, int serviceRequesterID, Spec spec, String question, String requestDate, int requestStatus, Profile serviceRequester,int requestID)
    {

        this.serviceType = serviceType;
        this.requestStatus = requestStatus;
        this.requestDate =requestDate;
        this.serviceRequesterID = serviceRequesterID;
        this.requestSpec = spec;
        this.question = question;
        this.serviceRequester = serviceRequester;
        this.requestID = requestID;
    }

    public Profile getServiceRequester() {
        return serviceRequester;
    }

    public void setServiceRequester(Profile serviceRequester) {
        this.serviceRequester = serviceRequester;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Spec getRequestSpec() {
        return requestSpec;
    }

    public void setRequestSpec(Spec requestSpec) {
        this.requestSpec = requestSpec;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public int getServiceRequesterID() {
        return serviceRequesterID;
    }

    public void setServiceRequesterID(int serviceRequesterID) {
        this.serviceRequesterID = serviceRequesterID;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }


    @Override
    public int compareTo(Request request2)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date2 = sdf.parse(request2.getRequestDate());
            date1 = sdf.parse(getRequestDate());
            Log.d("RequestCompare",date2.compareTo(date1)+" Is the result");
            return date2.compareTo(date1);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("RequestCompare","Catch");
        }
        return 0;
    }
}
