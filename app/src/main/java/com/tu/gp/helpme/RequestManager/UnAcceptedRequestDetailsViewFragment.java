package com.tu.gp.helpme.RequestManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.ProfileManager;
import com.tu.gp.helpme.R;
import com.tu.gp.helpme.ResponsManager.MyResponsesViewFragment;
import com.tu.gp.helpme.ResponsManager.ResponseManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UnAcceptedRequestDetailsViewFragment extends Fragment {

    EditText etSpec,etRequester,etStatus,etServiceType,etQuestion;
    Request request;
    Button btnRespond;
    Profile serviceRequester;
    ProgressBar pBUnAcceptedRequestDetails;
    LinearLayout lLUnAcceptedRequestDetails;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_un_request_details_view, container, false);
        etQuestion = view.findViewById(R.id.etQuestion);
        etSpec = view.findViewById(R.id.etSpec);
        etRequester = view.findViewById(R.id.etRequester);
        etStatus = view.findViewById(R.id.etStatus);
        etServiceType = view.findViewById(R.id.etServiceType);
        lLUnAcceptedRequestDetails = view.findViewById(R.id.lLUnAcceptedRequestDetails);
        pBUnAcceptedRequestDetails = view.findViewById(R.id.pBUnAcceptedRequestDetails);
        btnRespond = view.findViewById(R.id.btnRespond);
        request = UnAcceptedRequestsViewFragment.selectedRequest;

        MainActivity.showProgress(true,lLUnAcceptedRequestDetails,pBUnAcceptedRequestDetails);
        GetServiceRequesterTask getServiceRequesterTask = new GetServiceRequesterTask();
        getServiceRequesterTask.execute(request.getServiceRequesterID());

        String serviceType = "";
        switch (request.getServiceType())
        {
            case 0:
                serviceType = "Tutoring";
                break;
            case 1:
                serviceType = "Inquiry";
                break;
            case 2:
                serviceType = "Campus-Tour";
                break;
        }

        String status = "";
        switch (request.getRequestStatus())
        {
            case 0:
                status = "Waiting For Response";
                break;
            case 1:
                status = "Has Some Responses";
                break;
        }
        etQuestion.setText(request.getQuestion());
        etServiceType.setText(serviceType);
        etStatus.setText(status);
        etSpec.setText(request.getRequestSpec().getSpecName());
        //onPostExe etServiceRequester.setText...

        btnRespond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                MainActivity.showProgress(true,lLUnAcceptedRequestDetails,pBUnAcceptedRequestDetails);
                respondToRequest();
            }
        });
        return view;
    }

    private void respondToRequest()
    {
        RespondToRequestTask respondToRequestTask = new RespondToRequestTask();
        respondToRequestTask.execute();
    }
/*
    private void showProgress(final boolean show,View layot,View progres)
    {
        if (show) {

            layot.setVisibility(View.GONE);
            progres.setVisibility(View.VISIBLE);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
            Log.d("ShouldBeSpinning", "LOOL");
        } else {
            layot.setVisibility(View.VISIBLE);
            progres.setVisibility(View.GONE);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
    }
    */
    public class GetServiceRequesterTask extends AsyncTask<Integer,Void,Profile>
    {

        @Override
        protected Profile doInBackground(Integer... profileID)
        {
            return ProfileManager.getProfileByID(profileID[0]);
        }

        @Override
        protected void onPostExecute(Profile profile) {
            super.onPostExecute(profile);
            MainActivity.showProgress(false,lLUnAcceptedRequestDetails,pBUnAcceptedRequestDetails);
            serviceRequester = profile;
            etRequester.setText(serviceRequester.getFullName());
        }
    }
    public class RespondToRequestTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... voids)
        {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String currentDate=format.format(date);
            return ResponseManager.respondToRequest(request.getServiceType(),request.getServiceRequesterID()
            ,request.getRequestSpec(),request.getQuestion(),request.getRequestDate(),request.getRequestStatus()
            ,AuthenticationManager.profileID,currentDate,request.getRequestID(),request.getServiceRequester());
        }

        @Override
        protected void onPostExecute(String result)
        {

            MainActivity.showProgress(false,lLUnAcceptedRequestDetails,pBUnAcceptedRequestDetails);
            MainActivity.changeFragment(new MyResponsesViewFragment());
            MainActivity.nNavView.setSelectedItemId(R.id.nMyResponses);
            Log.d("RespondToRequest",result);
        }
    }
}
