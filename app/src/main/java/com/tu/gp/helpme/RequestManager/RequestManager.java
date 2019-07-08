package com.tu.gp.helpme.RequestManager;

import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.FeedBackManager.FeedBackManager;
import com.tu.gp.helpme.ProfileManager.Spec;
import com.tu.gp.helpme.ResponsManager.Response;

import java.util.List;

public class RequestManager
{
   public static String createNewRequest(int serviceType, int serviceRequesterID,
                                         Spec requestSpec, String question, String requestDate,
                                         int requestStatus)
   {
       Request request = new Request(serviceType,serviceRequesterID,requestSpec,
               question,requestDate,requestStatus);
       String results = DataManager.addNewRequestToDatabase(request);
       request.setRequestID(DataManager.getLatestValueInColumn("request"
               ,"requestID"));
       return results;
   }

    public static List<Request> getMyRequests(int profileID)
    {
        return DataManager.getProfileRquests(profileID);
    }

    public static List<Request> getUnAcceptedRequests(int profileID)
    {
        return DataManager.getUnAcceptedRequests(profileID);
    }

    public static List<Response> getRequestsResponses(int requestID)
    {
        return DataManager.getRequestResponses(requestID);
    }

    public static String updateRequestStatus(int requestID, int newStatus)
    {
       return DataManager.updateRequestStatus(requestID,newStatus);
    }

    public static String finishRequest(int requestID, int serviceRequesterID, int serviceProviderID, int rate, String feedBackNotes)
    {

        String writeNewFeedBack = FeedBackManager.writeNewFeedBack(requestID,serviceRequesterID,serviceProviderID,rate,feedBackNotes);
        String updateRequestStatus = RequestManager.updateRequestStatus(requestID,4);
        if(writeNewFeedBack !=null && updateRequestStatus != null)
        {
            return "write feedBack: "+writeNewFeedBack+" update: "+updateRequestStatus;
        }else if(writeNewFeedBack != null && updateRequestStatus == null)
        {
            return "write feedBack: "+writeNewFeedBack;
        }else if(writeNewFeedBack == null && updateRequestStatus != null)
        {
            return "update: "+updateRequestStatus;
        }
        else
        {
            return "Success "+writeNewFeedBack+" , "+updateRequestStatus;
        }
    }

    public static String deleteRequest(int requestID)
    {
        return DataManager.deleteRequest(requestID);
    }
}
