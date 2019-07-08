package com.tu.gp.helpme.RequestManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.ProfileManager.Spec;
import com.tu.gp.helpme.R;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MyRequestsViewFragment extends Fragment
{
    ListView lvMyRequests;
    List<Request> myRequests;
    private ProgressBar pBMyRequests;
    private RelativeLayout rLMyRequests;
    MyRequestsListAdapter listAdapter;
    FloatingActionButton btnNewRequest;
    TextView tvNoRequests;
    List<Spec> allSpecslist;
    public static Request selectedRequest;
    Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_requests_view, container, false);
        GetRequestsTask getRequestsTask = new GetRequestsTask();
        getRequestsTask.execute();
        lvMyRequests = view.findViewById(R.id.lvMyRequests);
        pBMyRequests = view.findViewById(R.id.pBMyRequests);
        rLMyRequests = view.findViewById(R.id.rLMyRequests);
        btnNewRequest = view.findViewById(R.id.btnNewRequest);
        tvNoRequests = view.findViewById(R.id.tvNoRequests);
        allSpecslist = new LinkedList<>();
        MainActivity.showProgress(true,rLMyRequests,pBMyRequests);
        btnNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateNewRequestDialog();
            }
        });

        lvMyRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRequest = myRequests.get(position);
                MainActivity.changeFragment(new RequestDetailsViewFragment());
            }
        });
        return view;
    }

    LinearLayout lLNewRequestDialog;
    ProgressBar pBNewRequestDialog;
    Spinner sSelectSpecForRequest;
    ArrayAdapter<Spec> arrayAdapter;
    int sericeType;
    String qustion;
    Spec selectedSpec;

    private void showCreateNewRequestDialog()
    {
        AlertDialog.Builder newRequestBuilder = new AlertDialog.Builder(getContext());
        final View newRequestView = getLayoutInflater().inflate(R.layout.dialog_new_request,null);
        final RadioButton rbTutoring = newRequestView.findViewById(R.id.rbTutoring);
        final RadioButton rbCampusTour = newRequestView.findViewById(R.id.rbCampusTour);
        final RadioButton rbInquires = newRequestView.findViewById(R.id.rbInquires);
        lLNewRequestDialog = newRequestView.findViewById(R.id.lLNewRequestDialog);
        pBNewRequestDialog = newRequestView.findViewById(R.id.pBNewRequestDialog);
        sSelectSpecForRequest = newRequestView.findViewById(R.id.sSelectSpecForRequest);
        final EditText etRequestQuestion = newRequestView.findViewById(R.id.etRequestQuestion);
        GetSpecsTask getSpecsTask = new GetSpecsTask();
        getSpecsTask.execute();
        newRequestBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
            @Override public void onClick(DialogInterface dialog, int which) {
                MainActivity.showProgress(true,rLMyRequests,pBMyRequests);
                sericeType = 0;
                if(rbInquires.isChecked()){
                    sericeType=1;}
                    else if(rbCampusTour.isChecked()){
                    sericeType = 2;}
                selectedSpec = (Spec)sSelectSpecForRequest.getSelectedItem();
                qustion = etRequestQuestion.getText().toString();
                AddNewRequestTask addNewRequestTask = new AddNewRequestTask();
                addNewRequestTask.execute();}});
                newRequestBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override public void onClick(DialogInterface dialog, int which){Toast.makeText(getContext(),"Canceled",Toast.LENGTH_LONG).show();}});
        newRequestBuilder.setView(newRequestView);
        final AlertDialog newRequestDialog = newRequestBuilder.create();
        newRequestDialog.show();
        newRequestDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        rbTutoring.setChecked(true);
        rbCampusTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRequestDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                etRequestQuestion.setVisibility(View.GONE); }});
        rbInquires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etRequestQuestion.getText().toString().isEmpty()) {newRequestDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
                etRequestQuestion.setVisibility(View.VISIBLE); }});
        rbTutoring.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (etRequestQuestion.getText().toString().isEmpty()) {
                    newRequestDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }etRequestQuestion.setVisibility(View.VISIBLE); }});
        etRequestQuestion.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count){
                if(s.length()<=0) {newRequestDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
                    else{newRequestDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true); }}
            @Override public void afterTextChanged(Editable s){}});}


    public class AddNewRequestTask extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... voids)
        {

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String currentDate=format.format(date);
            Log.d("currentDate",currentDate);
            return RequestManager.createNewRequest(sericeType,AuthenticationManager.profileID,selectedSpec,qustion, currentDate,0);
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result == null)
            {
                Toast.makeText(context,"Created New request Successfully",Toast.LENGTH_SHORT).show();
                Log.d("New Request","Created New request Successfully");
                MainActivity.changeFragment(new MyRequestsViewFragment());
            }else
            {
                MainActivity.showProgress(false,rLMyRequests,pBMyRequests);
                Log.d("New Request","There Was An Errot: "+result);
                Toast.makeText(context,"There Was An Errot: "+result,Toast.LENGTH_SHORT).show();

            }
        }
    }
    public class GetRequestsTask extends AsyncTask<Integer, List<Request>, List<Request>>
    {

        @Override
        protected List<Request> doInBackground(Integer... ints)
        {
            return RequestManager.getMyRequests(AuthenticationManager.profileID);
        }

        @Override
        protected void onPostExecute(List<Request> requests)
        {
            MainActivity.showProgress(false,rLMyRequests,pBMyRequests);
            myRequests = requests;
            if (myRequests == null) {
                Log.d("TAG", "There Are No Requests");
                tvNoRequests.setVisibility(View.VISIBLE);
                lvMyRequests.setVisibility(View.GONE);
            }else
            {

                listAdapter = new MyRequestsListAdapter(context,myRequests);
                lvMyRequests.setAdapter(listAdapter);
            }

        }

    }
    public class GetSpecsTask extends AsyncTask<Void, List<Spec>, List<Spec>> {
        @Override
        protected List<Spec> doInBackground(Void... voids) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.showProgress(true,lLNewRequestDialog,pBNewRequestDialog);

                }
            });

            return DataManager.getAllSpecs();

        }
        @Override
        protected void onPostExecute(List<Spec> specs)
        {
            MainActivity.showProgress(false,lLNewRequestDialog,pBNewRequestDialog);
            arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, specs);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sSelectSpecForRequest.setAdapter(arrayAdapter);

        }
    }
}
