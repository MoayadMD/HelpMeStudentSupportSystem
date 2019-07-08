package com.tu.gp.helpme.DataManager;


import android.util.Log;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.FeedBackManager.FeedBack;
import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.Spec;
import com.tu.gp.helpme.RequestManager.Request;
import com.tu.gp.helpme.ResponsManager.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class DataManager
{

    public static String addNewProfileToDatabase(Profile profile)
    {

        String query = "INSERT INTO profile(`password`, `fullName`, `email`, `phoneNumber`, `type`,`rating`)" +
                "VALUES ('"+profile.getPassword()+"','"+profile.getFullName()+"','"+profile.getEmail()
                +"','"+profile.getphoneNumber()+"',"+profile.getType()+","+0+")";
        return addToDB(query);

    }
    public static String addResponseToRequest(int requestID,int responseID)
    {
        String query = "INSERT INTO requestResponse(requestID,responseID) VALUES ("+requestID+","+responseID+")";
        return addToDB(query);
    }

    public static String addNewResponse(Response response)
    {
        String query = "INSERT INTO response(`requestID`, `serviceProviderID`, `responseDate`)" +
                "VALUES("+response.getRequestID()+","+response.getServiceProviderID()+",'"+response.getResponseDate()+"')";
        return addToDB(query);
    }
    public static String addNewRequestToDatabase(Request request)
    {
        String query = "INSERT INTO request(`status`, `requestDate`, `serviceType`, `specID`, `question`, `profileID`)" +
                "VALUES ("+request.getRequestStatus()+",'"+request.getRequestDate()
                +"',"+request.getServiceType()+","+request.getRequestSpec().getSpecID()+",'"+request.getQuestion()+"',"+request.getServiceRequesterID()+")";
        return addToDB(query);
    }
    public static String addRequestToProfile(int requestID,int profileID)
    {
        String query = "INSERT INTO profileRequest(profileID,requestID) VALUES("+profileID+","+requestID+")";
        return addToDB(query);
    }
    public static String updateRequestStatus(int requestID,int newStatus)
    {
        Log.d("RequestStatus",requestID+" status "+newStatus);
        String query = "UPDATE request SET status= "+newStatus+" WHERE requestID="+requestID;
        return getDataFromDatabase(query);
    }
    public static Spec getSpecByID(int specID)
    {
        Log.d("SpecID=",specID+"");
        String jsonSpec = getDataFromDatabase("SELECT * FROM spec WHERE specID="+specID);
        try
        {


            JSONArray jsonArray= new JSONArray(jsonSpec);
            Spec spec = new Spec(((JSONObject)jsonArray.get(0)).getString("specName"));
            spec.setSpecID(specID);
            return spec;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Request getRequestByID(int requestID)
    {
        String jsonRequest = getDataFromDatabase("SELECT * FROM request WHERE requestID="+requestID);
        try
        {

            JSONArray jsonArray= new JSONArray(jsonRequest);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            Request request = new Request
                    (
                            jsonObject.getInt("requestID"),
                            jsonObject.getInt("serviceType"),
                            jsonObject.getInt("profileID"),
                            getSpecByID(jsonObject.getInt("specID")),
                            jsonObject.getString("question"),
                            jsonObject.getString("requestDate"),
                            jsonObject.getInt("status")
                    );
            request.setServiceRequester(getProfileByID(jsonObject.getInt("profileID")));

            return request;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String modifyProfile(String fullName,String password,String email,String phoneNumber,boolean type,List<Spec> mySpec,int profileID)
    {
        String query = "UPDATE profile SET fullName='"+fullName+"', password='"+password+
                "', email='"+email+"', phoneNumber='"+phoneNumber+"', type ="+type+" WHERE profileID = "+profileID;
        String result =addToDB(query);
        result+=modifySpecs(profileID,mySpec);
        Log.d("ModifyProfile",result);
        return result;
    }
    private static String modifySpecs(int profileID,List<Spec> specs)
    {
        String query = "DELETE FROM profilespec WHERE profileID="+profileID;
        String result = addToDB(query);
        Log.d("ModifySpec","Add: "+result);
        for(Spec spec:specs)
        {
           result += addSpecToProfile(profileID,spec.getSpecID());
        }
        return result;
    }

    public static List<Request> getUnAcceptedRequests(int profileID)
    {
        List<Request> unAcceptedRequest = new LinkedList<>();
        String query = "SELECT * FROM request WHERE (status=0 OR status=1) AND profileID !="+profileID;
        String jsonUnAcceptedRequests = getDataFromDatabase(query);
        Log.d("get unAcceptedRequests",jsonUnAcceptedRequests);
        try
        {
            JSONArray jsonArray = new JSONArray(jsonUnAcceptedRequests);
            for(int i = 0 ; i <jsonArray.length();i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Request request = new Request
                        (
                                jsonObject.getInt("requestID"),
                                jsonObject.getInt("serviceType"),
                                jsonObject.getInt("profileID"),
                                getSpecByID(jsonObject.getInt("specID")),
                                jsonObject.getString("question"),
                                jsonObject.getString("requestDate"),
                                jsonObject.getInt("status")
                        );
                request.setServiceRequester(getProfileByID(jsonObject.getInt("profileID")));
                if(!chickServiceProvider(request.getRequestID(),profileID) && chickSpec(profileID,request)) {
                    unAcceptedRequest.add(request);
                }
            }
            return unAcceptedRequest;
        } catch (JSONException e) {
            Log.d("get unAcceptedRequests","Could not get requests"+jsonUnAcceptedRequests);
            return null;
        }
    }

    private static boolean chickSpec(int profileID, Request request)
    {
        for(Spec spec: getProfileSpecs(profileID))
        {
            if(spec.getSpecID() == request.getRequestSpec().getSpecID())
            {
                Log.d("ChickSpec","Spec: "+spec.getSpecID()+" Request: "+request.getRequestSpec().getSpecID());
                return true;
            }
        }
        return false;
    }
    private static boolean chickServiceProvider(int requestID,int profileID)
    {
      List<Response> responses = getRequestResponses(requestID);
      if(responses !=null)
      {
          Log.d("Chick","Not null");
          for (Response response : responses)
          {
              if (response.getServiceProviderID() == profileID)
                  return true;
          }
          return false;
      }else
          Log.d("Chick","null");
          return false;
    }

    public static List<Response> getProfileResponses(int profileID) {
        List<Response> profileResponses = new LinkedList<>();
        String jsonResponses = getDataFromDatabase("SELECT * FROM response WHERE serviceProviderID =" + profileID);
        Log.d("getProfileResponses", jsonResponses);
        try {
            JSONArray jsonArray = new JSONArray(jsonResponses);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Request responsedRequest = getRequestByID(jsonObject.getInt("requestID"));
                Response response = new Response
                        (
                                 responsedRequest.getServiceType()
                                ,responsedRequest.getServiceRequesterID()
                                ,responsedRequest.getRequestSpec()
                                ,responsedRequest.getQuestion()
                                ,responsedRequest.getRequestDate()
                                ,responsedRequest.getRequestStatus()
                                ,jsonObject.getInt("serviceProviderID")
                                ,jsonObject.getString("responseDate")
                                ,responsedRequest.getServiceRequester()
                                ,jsonObject.getInt("requestID")
                        );
                response.setResponseID(jsonObject.getInt("responseID"));
                Log.d("ResponsedRequestID",responsedRequest.getRequestID()+"");
                profileResponses.add(response);
            }
            return profileResponses;
        } catch (JSONException e) {
            Log.d("get PR", "Could not get profile responses" + jsonResponses);
            return null;
        }
    }
    public static Response getResponseByRequestID(int requestID)
    {
        String jsonResponse = getDataFromDatabase("SELECT * FROM response WHERE requestID="+requestID);
        Request request = getRequestByID(requestID);
        try
        {
            JSONArray jsonArray= new JSONArray(jsonResponse);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            Response response = new Response
                    (
                            request.getServiceType()
                            ,request.getServiceRequesterID()
                            ,request.getRequestSpec()
                            ,request.getQuestion()
                            ,request.getRequestDate()
                            ,request.getRequestStatus()
                            ,jsonObject.getInt("serviceProviderID")
                            ,jsonObject.getString("responseDate")
                            ,request.getServiceRequester()
                            ,jsonObject.getInt("requestID")
                    );
            response.setServiceProvider(getProfileByID(jsonObject.getInt("serviceProviderID")));
            return response;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static List<Response> getRequestResponses(int requestID)
    {
        List<Response> requestResponses = new LinkedList<>();
        String jsonResponses = getDataFromDatabase("SELECT * FROM response WHERE requestID =" + requestID);
        Request respondedRequest = getRequestByID(requestID);
        Log.d("getRequestResponses", jsonResponses);
        try {
            JSONArray jsonArray = new JSONArray(jsonResponses);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Response response = new Response
                        (
                                respondedRequest.getServiceType()
                                ,respondedRequest.getServiceRequesterID()
                                ,respondedRequest.getRequestSpec()
                                ,respondedRequest.getQuestion()
                                ,respondedRequest.getRequestDate()
                                ,respondedRequest.getRequestStatus()
                                ,jsonObject.getInt("serviceProviderID")
                                ,jsonObject.getString("responseDate")
                                ,respondedRequest.getServiceRequester()
                                ,jsonObject.getInt("requestID")
                        );
                Log.d("RequestID=",requestID+" !!");
                response.setRequestID(jsonObject.getInt("requestID"));
                response.setResponseID(jsonObject.getInt("responseID"));
                response.setServiceProvider(getProfileByID(jsonObject.getInt("serviceProviderID")));
                requestResponses.add(response);
            }
            return requestResponses;
        } catch (JSONException e) {
            Log.d("get RR", "Could not get request's responses" + jsonResponses);
            return null;
        }
    }
    public static List<Request> getProfileRquests(int profileID)
    {
        List<Request> profileRequests =new LinkedList<>();
        String jsonRequests = getDataFromDatabase("SELECT * FROM request WHERE profileID ="+profileID);
        Log.d("getPRofileRequests",jsonRequests);
        try
        {
            JSONArray jsonArray = new JSONArray(jsonRequests);
            for(int i = 0 ; i <jsonArray.length();i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Request request = new Request
                        (
                                jsonObject.getInt("requestID"),
                                jsonObject.getInt("serviceType"),
                                jsonObject.getInt("profileID"),
                                getSpecByID(jsonObject.getInt("specID")),
                                jsonObject.getString("question"),
                                jsonObject.getString("requestDate"),
                                jsonObject.getInt("status")
                        );
                request.setServiceRequester(getProfileByID(jsonObject.getInt("requestID")));
                profileRequests.add(request);
            }
            return profileRequests;
        } catch (JSONException e) {
            Log.d("get PR","Could not get profile requests"+jsonRequests);
            return null;
        }


    }
    private static String addToDB(String query)
    {
        try
        {
            String insertProfileUrl = Urls.getInsertDataUrl(URLEncoder.encode(query,"utf-8"));
            URL url = new URL(insertProfileUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            return (bufferedReader.readLine());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static int getLatestValueInColumn(String tabelName,String columnName)
    {
        String latestVal = getDataFromDatabase("SELECT MAX("+columnName+") FROM "+tabelName);
        try
        {
            Log.d("Latest Value= ",latestVal);
            JSONArray jsonArray = new JSONArray(latestVal);
            return ((JSONObject)jsonArray.get(0)).getInt("MAX("+columnName+")");
        }catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }

    }
    public static List<Spec> getAllSpecs() {
        List<Spec> specList = new LinkedList<>();
        String jsonSpecString = getDataFromDatabase("SELECT * FROM spec");
        JSONArray jsonArray = null;
        try
        {
            jsonArray = new JSONArray(jsonSpecString);
            for(int i = 0 ; i <jsonArray.length();i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Spec spec = new Spec(jsonObject.getString("specName"));
                spec.setSpecID(jsonObject.getInt("specID"));
                specList.add(spec);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return specList;
    }
    private static int getProfileIDFromDatabse(String profileUserName)
    {
        final int[] id = new int[1];
        String getProfileIDQuery="SELECT profileID FROM profile WHERE userName='"+profileUserName+"'";
        PostResponseAsyncTask getIDByUserName = new PostResponseAsyncTask(MainActivity.context, new AsyncResponse()
        {
            @Override
            public void processFinish(String s) {
                id[0] = Integer.parseInt(s);
            }
        });
        try {
            getIDByUserName.execute(Urls.getGetDataURL(URLEncoder.encode(getProfileIDQuery,"utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id[0];
    }
    public static String addNewSpecToDatabase(String specName)
    {
        String query = "INSERT INTO spec(`specName`) VALUES ('"+specName+"')";
        return addToDB(query);
    }

    public static String addSpecToProfile(int profileID, int specID)
    {
        String query ="INSERT INTO profilespec(profileID,specID) VALUES ("+profileID+","+specID+")";
        return addToDB(query);
    }

    private static String insertDataToDatabase(String query)
    {
        try
        {
            String authenticationUrl = Urls.getGetDataURL(URLEncoder.encode(query,"utf-8"));
            URL url = new URL(authenticationUrl);
            URLConnection urlConnection = url.openConnection();
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            String line;
            String results = "";

            return results;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getDataFromDatabase(String query)
    {
        try
        {
            String authenticationUrl = Urls.getGetDataURL(URLEncoder.encode(query,"utf-8"));
            URL url = new URL(authenticationUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String results = "";
            while((line = bufferedReader.readLine())!=null)
            {
                results+= line;
            }
            return results;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static int authenticate(String email, String password)
    {
        String authenticationQuery = "SELECT profileID FROM profile WHERE BINARY email='"+email+"' AND password='"+password+"'";
        String pID =  getDataFromDatabase(authenticationQuery);
        try
        {
            JSONArray jsonArray = new JSONArray(pID);
            return ((JSONObject)jsonArray.get(0)).getInt("profileID");
        }catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static void updateProfile(int profileID, String columnName,String newValue)
    {
        boolean bool;
        final String updateProfileQuery;
        if(columnName.equals("type"))
        {
            bool=Boolean.parseBoolean(newValue);
            updateProfileQuery = "UPDATE profile SET "+columnName+"="+bool+" WHERE profileID="+profileID+";";
        }else
        {
            updateProfileQuery = "UPDATE profile SET "+columnName+"='"+newValue+"' WHERE profileID="+profileID+";";
        }
        PostResponseAsyncTask updateProfile = new PostResponseAsyncTask(MainActivity.context, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                Log.d("updateProfile",s);
            }
        });
        try {
            updateProfile.execute(Urls.getInsertDataUrl(URLEncoder.encode(updateProfileQuery,"utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static Profile getProfileByID(int profileID)
    {
        String jsonRequest = getDataFromDatabase("SELECT * FROM profile WHERE profileID="+profileID);
        try
        {

            JSONArray jsonArray= new JSONArray(jsonRequest);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            Profile profile = new Profile
                    (
                            jsonObject.getString("fullName"),
                            jsonObject.getString("email"),
                            jsonObject.getString("phoneNumber"),
                            jsonObject.getInt("type")==1,
                            jsonObject.getInt("rating")
                    );
            profile.setListOfSpecs(getProfileSpecs(profileID));
            profile.setPassword(jsonObject.getString("password"));
            return profile;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Spec> getProfileSpecs(int profileID)
    {
        String jsonRequest = getDataFromDatabase("SELECT * FROM profilespec WHERE profileID="+profileID);
        List<Spec> profileSpecList = new LinkedList<>();
        try
        {

            JSONArray jsonArray= new JSONArray(jsonRequest);
            for(int i = 0 ; i< jsonArray.length();i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                profileSpecList.add(getSpecByID(jsonObject.getInt("specID")));
            }
            return profileSpecList;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String deleteUnSelectedResponses(int responseID, int requestID)
    {
        String query = "DELETE FROM response WHERE (requestID = "+requestID+" AND responseID <>"+responseID+")";
        return getDataFromDatabase(query);
    }
    public static String updateProfileRating(int profileID,int rating)
    {
        Profile profile = getProfileByID(profileID);
        int oldRating = profile.getRating();
        int newRating = (oldRating+rating)/2;
        String query = "UPDATE profile SET rating="+newRating+" WHERE profileID="+profileID;
        return getDataFromDatabase(query);
    }

    public static String addNewFeedBack(FeedBack feedBack)
    {
        String query = "INSERT INTO feedBack(writerID,recipientID,feedBackRate,feedBackNotes,requestID) " +
                "VALUES("+feedBack.getWriterID()+","+feedBack.getRecipientID()
                +","+feedBack.getRate()+",'"+feedBack.getNotes()+"',"+feedBack.getRequestID()+")";
        return addToDB(query);
    }

    public static String deleteRequestResponses(int requestID)
    {
        String query = " DELETE FROM response WHERE requestID="+requestID;
        return addToDB(query);
    }
    public static String deleteRequest(int requestID)
    {
        String query = " DELETE FROM request WHERE requestID="+requestID;
        return addToDB(query);
    }

    public static String deleteResponse(int responseID)
    {
        String query = "DELETE FROM response WHERE responseID="+responseID;
        return addToDB(query);
    }
}
