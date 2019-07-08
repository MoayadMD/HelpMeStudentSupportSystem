package com.tu.gp.helpme.ResponsManager;

import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.Spec;
import com.tu.gp.helpme.RequestManager.Request;

import java.util.Date;

public class Response extends Request
{
    int serviceProviderID,responseID;
    Profile serviceProvider;
    String responseDate,finishDate;
    public Response(int serviceType, int serviceRequesterID, Spec spec, String question, String requestDate,
                    int requestStatus,int serviceProviderID,String responseDate,Profile serviceRequester,int requestID)
    {
        super(serviceType, serviceRequesterID, spec, question, requestDate, requestStatus,serviceRequester,requestID);
        this.responseDate = responseDate;
        this.serviceProviderID = serviceProviderID;
    }


    public Profile getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(Profile serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public int getResponseID() {
        return responseID;
    }

    public void setResponseID(int responseID) {
        this.responseID = responseID;
    }

    public int getServiceProviderID() {
        return serviceProviderID;
    }

    public void setServiceProviderID(int serviceProviderID) {
        this.serviceProviderID = serviceProviderID;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public int compareTo(Request request2) {
        Date date1 = new Date(getRequestDate());
        Date date2 = new Date(request2.getRequestDate());
        return date1.compareTo(date2);
    }
}
