package com.tu.gp.helpme.ResponsManager;

import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.FeedBackManager.FeedBackManager;
import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.Spec;
import com.tu.gp.helpme.RequestManager.RequestManager;

import java.util.List;

public class ResponseManager
{
    public static String respondToRequest(int serviceType, int serviceRequesterID, Spec spec, String question, String requestDate,
                                          int requestStatus, int serviceProviderID, String responseDate
                                         ,int requestID, Profile serviceRequester)
    {
        Response response = new Response(serviceType,serviceRequesterID,spec,question,requestDate
                ,requestStatus,serviceProviderID,responseDate,serviceRequester,requestID);
        String result = "";
        String createNewResponse =DataManager.addNewResponse(response);
        String updateRequestStatus = RequestManager.updateRequestStatus(response.getRequestID(),1);
        if(createNewResponse == null)
        {
            result = "Created New Response, ";
        }else
        {
            result = "Failed Creating New Response "+createNewResponse+",\n ";
        }
        if( updateRequestStatus == null)
        {
            result+= "Updated Request Status";
        }else
        {
            result+= "Failed Updating Request Status "+updateRequestStatus;
        }

        return result;
    }

    public static String finishResponse(int requestID,int writerID,int recipientID,int rate,String notes)
    {
        String writeNewFeedBack = FeedBackManager.writeNewFeedBack(requestID,writerID,recipientID,rate,notes);
        String updateRequestStatus = RequestManager.updateRequestStatus(requestID,3);
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
    public static Response getResponseByRequestID(int requestID)
    {
        return DataManager.getResponseByRequestID(requestID);
    }
    public static String selectResponse(Response response)
    {
        String result;
        String deleteResponses  = DataManager.deleteUnSelectedResponses(response.getResponseID(),response.getRequestID());
        String updateRequestStatus = DataManager.updateRequestStatus(response.getRequestID(),2);

        if(deleteResponses == null)
        {
            result = "Deleted Un Selected Responses, ";
        }else
        {
            result = "Failed Deleting Un Selected Responses "+deleteResponses+",\n ";
        }
        if( updateRequestStatus == null)
        {
            result+= "Updated Request Status";
        }else
        {
            result+= "Failed Updating Request Status "+updateRequestStatus;
        }
        return  result;
    }
    public static List<Response> getMyResponses(int profileID)
    {
        return DataManager.getProfileResponses(profileID);
    }
    public static String cancelResponse(Response response)
    {
        String result= null;
        if(response.getRequestStatus() == 1)
        {
            result = DataManager.deleteResponse(response.getResponseID());
            if(DataManager.getRequestResponses(response.getRequestID()) == null)
            {
                result+= DataManager.updateRequestStatus(response.getRequestID(),0);
            }
        }else if(response.getRequestStatus() == 2)
        {
            result = DataManager.deleteResponse(response.getResponseID());
            result += DataManager.updateRequestStatus(response.getRequestID(),0);
        }
        return result;
    }

    public static String deleteRequestResponses(int requestID)
    {
        return DataManager.deleteRequestResponses(requestID);
    }

}
