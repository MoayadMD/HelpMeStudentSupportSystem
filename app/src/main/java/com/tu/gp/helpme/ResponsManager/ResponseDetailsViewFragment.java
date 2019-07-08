package com.tu.gp.helpme.ResponsManager;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RatingBar;
import android.widget.Toast;

import com.tu.gp.helpme.DataManager.Urls;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.ProfileManager;
import com.tu.gp.helpme.R;
import com.tu.gp.helpme.RequestManager.MyRequestsViewFragment;
import com.tu.gp.helpme.RequestManager.Request;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResponseDetailsViewFragment extends Fragment {
    EditText etSpec, etRequester, etStatus, etServiceType, etQuestion, etRequestDate, etResponseDate,etPhoneNumber;
    Request request;
    Button btnFinishResponse,btnCancelResponse;
    Profile serviceRequester;
    ProgressBar pBResponseDetails;
    LinearLayout lLResponseDetails;
    Response selectedResponse;
    Context context;
    FloatingActionButton btnWhatsapp,btnCall;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_response_details_view, container, false);

        etQuestion = view.findViewById(R.id.etQuestion);
        etSpec = view.findViewById(R.id.etSpec);
        etRequester = view.findViewById(R.id.etRequester);
        etStatus = view.findViewById(R.id.etStatus);
        etServiceType = view.findViewById(R.id.etServiceType);
        etRequestDate = view.findViewById(R.id.etRequestDate);
        etResponseDate = view.findViewById(R.id.etResponseDate);
        etPhoneNumber =  view.findViewById(R.id.etPhoneNumber);
        lLResponseDetails = view.findViewById(R.id.lLResponseDetails);
        pBResponseDetails = view.findViewById(R.id.pBResponseDetails);
        btnFinishResponse = view.findViewById(R.id.btnFinishResponse);
        btnCancelResponse = view.findViewById(R.id.btnCancelResponse);
        btnWhatsapp = view.findViewById(R.id.btnWhatsapp);
        btnCall = view.findViewById(R.id.btnCall);
        //request = MyRequestsViewFragment.selectedRequest;
        selectedResponse = MyResponsesViewFragment.mySelectedResponse;
        Log.e("SelectedResposnseRID",selectedResponse.getRequestID()+"");

        etQuestion.setText(selectedResponse.getQuestion());
        etSpec.setText(selectedResponse.getRequestSpec().getSpecName());
        etRequester.setText(selectedResponse.getServiceRequester().getFullName());
        etRequestDate.setText(selectedResponse.getRequestDate());
        etResponseDate.setText(selectedResponse.getResponseDate());
        etPhoneNumber.setText(selectedResponse.getServiceRequester().getPhoneNumber());
        String serviceType = "";
        switch (selectedResponse.getServiceType()) {
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
        switch (selectedResponse.getRequestStatus()) {
            case 0:
                status = "Waiting For Response";
                break;
            case 1:
                status = "Has Some Responses";
                btnCancelResponse.setVisibility(View.VISIBLE);
                etPhoneNumber.setVisibility(View.GONE);
                etRequester.setVisibility(View.GONE);
                break;
            case 2:
                status = "Active Request";
                btnCancelResponse.setVisibility(View.VISIBLE);
                btnWhatsapp.show();
                btnCall.show();
                break;
            case 3:
                status = "Waiting for requester's feedback";
                btnFinishResponse.setText("You are done here");
                btnFinishResponse.setEnabled(false);
                btnWhatsapp.show();
                btnCall.show();
                break;
            case 4:
                status = "Finished Request";
                btnFinishResponse.setText("You are done here");
                btnFinishResponse.setEnabled(false);
                btnWhatsapp.hide();
                btnCall.hide();
                break;
        }

        etStatus.setText(status);
        etServiceType.setText(serviceType);

        if (selectedResponse.getRequestStatus() > 1) {
            btnFinishResponse.setVisibility(View.VISIBLE);
        }

        btnFinishResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showFeedBackDialog();
            }
        });
        btnCancelResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                MainActivity.showProgress(true,lLResponseDetails,pBResponseDetails);
                cancelResponse();
            }
        });


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+selectedResponse.getServiceRequester().getPhoneNumber()));
                startActivity(intent);
            }
        });

        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = Urls.getWhatsappURL(selectedResponse.getServiceRequester().getPhoneNumber());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        return view;
    }

    private void cancelResponse()
    {
        CancelResponseTask cancelResponseTask = new CancelResponseTask();
        cancelResponseTask.execute();
    }

    RatingBar rbFeedBack;
    EditText etFeedBack;

    private void showFeedBackDialog() {
        final AlertDialog.Builder feedBackBuilder = new AlertDialog.Builder(getContext());
        final View feedBackView = getLayoutInflater().inflate(R.layout.dialog_feed_back, null);
        rbFeedBack = feedBackView.findViewById(R.id.rBFeedBack);
        etFeedBack = feedBackView.findViewById(R.id.etFeedBack);
        rbFeedBack.setRating(rbFeedBack.getNumStars());

        feedBackBuilder.setPositiveButton("Conferm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.showProgress(true, lLResponseDetails, pBResponseDetails);

                FinishResponseTask finishResponseTask = new FinishResponseTask();
                finishResponseTask.execute(rbFeedBack.getRating()+"",etFeedBack.getText().toString());
            }
        });

        feedBackBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Canceled", Toast.LENGTH_LONG).show();
            }
        });

        feedBackBuilder.setView(feedBackView);
        AlertDialog selectResponseDialog = feedBackBuilder.create();
        selectResponseDialog.show();
    }
    private class FinishResponseTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            int rate = (int) Math.round(Double.parseDouble(strings[0]));
            String feedBackNotes = strings[1];
            Log.d("FeedBackReqID",selectedResponse.getRequestID()+" ");
            return ResponseManager.finishResponse(selectedResponse.getRequestID(), selectedResponse.getServiceProviderID()
                    ,selectedResponse.getServiceRequesterID(),rate,feedBackNotes);
        }

        @Override
        protected void onPostExecute(String s)
        {
            MainActivity.showProgress(false, lLResponseDetails, pBResponseDetails);
            MainActivity.changeFragment(new MyResponsesViewFragment());
            Log.d("FinishResponse",s);
        }
    }

    private class CancelResponseTask extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... voids)
        {
            return ResponseManager.cancelResponse(selectedResponse);
        }

        @Override
        protected void onPostExecute(String s)
        {
            MainActivity.showProgress(false, lLResponseDetails, pBResponseDetails);
            if(s == null || s.contains("null"))
            {
                MainActivity.changeFragment(new MyResponsesViewFragment());
                Toast.makeText(context,"Successfuly Canceled",Toast.LENGTH_SHORT).show();
            }else
            {
                Log.d("Result: ",s);
                Toast.makeText(context,"Faield: "+s,Toast.LENGTH_SHORT).show();
            }



        }
    }
}
