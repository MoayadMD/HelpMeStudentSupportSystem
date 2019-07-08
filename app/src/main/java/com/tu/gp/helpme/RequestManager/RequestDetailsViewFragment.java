package com.tu.gp.helpme.RequestManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.DataManager.Urls;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.ProfileManager;
import com.tu.gp.helpme.ProfileManager.Spec;
import com.tu.gp.helpme.R;
import com.tu.gp.helpme.ResponsManager.MyResponsesListAdapter;
import com.tu.gp.helpme.ResponsManager.MyResponsesViewFragment;
import com.tu.gp.helpme.ResponsManager.Response;
import com.tu.gp.helpme.ResponsManager.ResponseDetailsViewFragment;
import com.tu.gp.helpme.ResponsManager.ResponseManager;

import java.util.List;

public class RequestDetailsViewFragment extends Fragment {

    EditText etSpec,etRequester,etStatus,etServiceType,etQuestion,etServiceProvider;
    Request request;
    Button btnRateServiceProvider,btnCancelRequest;
    Profile serviceRequester;
    ProgressBar pBRequestDetails;
    LinearLayout lLRequestDetails;
    Response selectedResponse;
    ListView lvRequestsResponses;
    List<Response> requestsResponse;
    RequestsResponsesListAdapter listAdapter;
    Context context;
    FloatingActionButton btnWhatsapp,btnCall;
    TextView tvResponses;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_details_view, container, false);
        etQuestion = view.findViewById(R.id.etQuestion);
        etSpec = view.findViewById(R.id.etSpec);
        etRequester = view.findViewById(R.id.etRequester);
        etStatus = view.findViewById(R.id.etStatus);
        etServiceType = view.findViewById(R.id.etServiceType);
        lLRequestDetails = view.findViewById(R.id.lLRequestDetails);
        pBRequestDetails = view.findViewById(R.id.pBRequestDetails);
        btnRateServiceProvider = view.findViewById(R.id.btnRateServiceProvider);
        lvRequestsResponses = view.findViewById(R.id.lvRequestsResponses);
        etServiceProvider = view.findViewById(R.id.etServiceProvider);
        btnCancelRequest = view.findViewById(R.id.btnCancelRequest);
        btnWhatsapp = view.findViewById(R.id.btnWhatsapp);
        btnCall = view.findViewById(R.id.btnCall);
        tvResponses = view.findViewById(R.id.tvResponses);

        request = MyRequestsViewFragment.selectedRequest;
        MainActivity.showProgress(true,lLRequestDetails,pBRequestDetails);

        GetServiceRequesterTask getServiceRequesterTask = new GetServiceRequesterTask();
        GetMyRequestResponse getMyRequestResponse = new GetMyRequestResponse();

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
                tvResponses.setText("");
                break;
            case 1:
                status = "Has Some Responses";
                break;
            case 2:
                status = "Active Request";
                getMyRequestResponse.execute(request.getRequestID());
                lvRequestsResponses.setVisibility(View.GONE);
                btnCall.show();
                btnWhatsapp.show();
                break;
            case 3:
                status = "Waiting for requester's feedback";
                getMyRequestResponse.execute(request.getRequestID());
                lvRequestsResponses.setVisibility(View.GONE);
                btnRateServiceProvider.setVisibility(View.VISIBLE);
                btnCancelRequest.setVisibility(View.INVISIBLE);
                btnCall.show();
                btnWhatsapp.show();
                break;
            case 4:
                status = "Finished Request";
                getMyRequestResponse.execute(request.getRequestID());
                lvRequestsResponses.setVisibility(View.GONE);
                btnCancelRequest.setVisibility(View.INVISIBLE);

                btnCall.hide();
                btnWhatsapp.hide();
                break;
        }
        etQuestion.setText(request.getQuestion());
        etServiceType.setText(serviceType);
        etStatus.setText(status);
        etSpec.setText(request.getRequestSpec().getSpecName());
        //onPostExe etServiceRequester.setText...
        btnRateServiceProvider.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showFeedBackDialog();
            }
        });

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showProgress(true,lLRequestDetails,pBRequestDetails);
                cancelRequest();
            }
        });
        // ^^^ Done With request details ^^^
        // vvv Responses List vvv

        GetRequestsResponsesTask getResponsesTask= new GetRequestsResponsesTask();
        getResponsesTask.execute(request.getRequestID());
        if(request.getRequestStatus()<2)
        {
            lvRequestsResponses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedResponse = requestsResponse.get(position);
                        showSelecetResponseDialog(position);
                    return false;
                }
            });
        }

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+selectedResponse.getServiceProvider().getPhoneNumber()));
                startActivity(intent);
            }
        });

        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = Urls.getWhatsappURL(selectedResponse.getServiceProvider().getPhoneNumber());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void cancelRequest()
    {
        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.execute();
    }

    RatingBar rbFeedBack;
    EditText etFeedBack;
    private void showFeedBackDialog() {
        final AlertDialog.Builder feedBackBuilder = new AlertDialog.Builder(context);
        final View feedBackView = getLayoutInflater().inflate(R.layout.dialog_feed_back, null);
        rbFeedBack = feedBackView.findViewById(R.id.rBFeedBack);
        etFeedBack = feedBackView.findViewById(R.id.etFeedBack);
        rbFeedBack.setRating(rbFeedBack.getNumStars());


        feedBackBuilder.setPositiveButton("Conferm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.showProgress(true, lLRequestDetails, pBRequestDetails);
                FinishRequestTask finishRequestTask= new FinishRequestTask();
                finishRequestTask.execute(rbFeedBack.getRating()+"",etFeedBack.getText().toString());
            }
        });

        feedBackBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Canceled", Toast.LENGTH_LONG).show();
            }
        });

        feedBackBuilder.setView(feedBackView);
        AlertDialog selectResponseDialog = feedBackBuilder.create();
        selectResponseDialog.show();
    }


    TextView tvConfermResponse;
    private void showSelecetResponseDialog(final int i)
    {
        final AlertDialog.Builder selectResponseBuilder = new AlertDialog.Builder(context);
        final View selectResponseView = getLayoutInflater().inflate(R.layout.dialog_select_response,null);
        tvConfermResponse = selectResponseView.findViewById(R.id.tvConfermResponse);
        tvConfermResponse.setText("Select ( "+requestsResponse.get(i).getServiceProvider().getFullName()+" ) as a service provider?");

        selectResponseBuilder.setPositiveButton("Conferm", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MainActivity.showProgress(true,lLRequestDetails,pBRequestDetails);
                SelectResponseTask selectResponseTask = new SelectResponseTask();
                selectResponseTask.execute();
            }
        });

        selectResponseBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(context,"Canceled",Toast.LENGTH_LONG).show();
            }
        });

        selectResponseBuilder.setView(selectResponseView);
        AlertDialog selectResponseDialog = selectResponseBuilder.create();
        selectResponseDialog.show();
    }

    public class GetMyRequestResponse extends AsyncTask<Integer,Void,Response>
    {

        @Override
        protected Response doInBackground(Integer... integers)
        {
            MainActivity.showProgress(true,lLRequestDetails,pBRequestDetails);
            return ResponseManager.getResponseByRequestID(integers[0]);
        }

        @Override
        protected void onPostExecute(Response response)
        {
            MainActivity.showProgress(false,lLRequestDetails,pBRequestDetails);
            selectedResponse = response;
            etServiceProvider.setVisibility(View.VISIBLE);
            etServiceProvider.setText(selectedResponse.getServiceProvider().getFullName());
        }
    }
    public class GetRequestsResponsesTask extends AsyncTask<Integer, List<Response>, List<Response>>
    {

        @Override
        protected List<Response> doInBackground(Integer... ints)
        {
            return RequestManager.getRequestsResponses(ints[0]);
        }

        @Override
        protected void onPostExecute(List<Response> responses)
        {
            MainActivity.showProgress(false,lLRequestDetails,pBRequestDetails);
            requestsResponse = responses;
            if (requestsResponse == null) {
                Log.d("TAG", "There Are No Requests");
                /*tvNoResponses.setVisibility(View.VISIBLE);
                lvMyResponses.setVisibility(View.GONE);*/
            }else
            {
                listAdapter = new RequestsResponsesListAdapter(context,requestsResponse);
                lvRequestsResponses.setAdapter(listAdapter);
            }

        }

    }

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
            serviceRequester = profile;
            etRequester.setText(serviceRequester.getFullName());
        }
    }


    private class SelectResponseTask extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... voids)
        {
            Log.d("SelectedResponse:",selectedResponse.getResponseID()+", "+selectedResponse.getRequestID());
            return ResponseManager.selectResponse(selectedResponse);
        }

        @Override
        protected void onPostExecute(String result)
        {
            MainActivity.showProgress(false,lLRequestDetails,pBRequestDetails);
            Log.d("SelectResponse",result);
            lvRequestsResponses.setVisibility(View.GONE);
            MainActivity.changeFragment(new MyRequestsViewFragment());

        }
    }

    private class FinishRequestTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            int rate = (int) Math.round(Double.parseDouble(strings[0]));
            String feedBackNotes = strings[1];
            return RequestManager.finishRequest(selectedResponse.getRequestID(), selectedResponse.getServiceRequesterID()
                    ,selectedResponse.getServiceProviderID(),rate,feedBackNotes);
        }

        @Override
        protected void onPostExecute(String s)
        {
            MainActivity.showProgress(false, lLRequestDetails, pBRequestDetails);
            btnRateServiceProvider.setVisibility(View.GONE);
            MainActivity.changeFragment(new MyRequestsViewFragment());
            Log.d("FinishRequest",s);
        }
    }

    public class CancelRequest extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... voids)
        {
           return RequestManager.deleteRequest(request.getRequestID());
        }

        @Override
        protected void onPostExecute(String s)
        {
            MainActivity.showProgress(false,lLRequestDetails,pBRequestDetails);
            if(s == null)
            {
                MainActivity.changeFragment(new MyRequestsViewFragment());
                Toast.makeText(context,"Successfuly Canceled",Toast.LENGTH_SHORT).show();
            }else
            {
                Log.d("Result: ",s);
                Toast.makeText(context,"Faield: "+s,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
