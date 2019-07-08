package com.tu.gp.helpme.ResponsManager;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.R;
import com.tu.gp.helpme.RequestManager.MyRequestsListAdapter;
import com.tu.gp.helpme.RequestManager.MyRequestsViewFragment;
import com.tu.gp.helpme.RequestManager.Request;
import com.tu.gp.helpme.RequestManager.RequestManager;
import com.tu.gp.helpme.RequestManager.UnAcceptedRequestsViewFragment;

import java.util.LinkedList;
import java.util.List;


public class MyResponsesViewFragment extends Fragment {

    ListView lvMyResponses;
    List<Response> myResponse;
    private ProgressBar pBMyResponse;
    private RelativeLayout rLMyResponse;
    MyResponsesListAdapter listAdapter;
    FloatingActionButton btnNewResponse;
    TextView tvNoResponses;
    static Response mySelectedResponse;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_responses_view, container, false);
        lvMyResponses = view.findViewById(R.id.lvMyResponses);
        pBMyResponse = view.findViewById(R.id.pBMyResponses);
        rLMyResponse = view.findViewById(R.id.rLMyResponses);
        btnNewResponse = view.findViewById(R.id.btnNewResponses);
        tvNoResponses = view.findViewById(R.id.tvNoResponses);
        MainActivity.showProgress(true,rLMyResponse,pBMyResponse);
        GetResponsesTask getResponsesTask= new GetResponsesTask();
        getResponsesTask.execute();
        lvMyResponses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mySelectedResponse = myResponse.get(position);
                Log.d("RequesterName",myResponse.get(position).getRequestID()+"");
                MainActivity.changeFragment(new ResponseDetailsViewFragment());
            }
        });
        btnNewResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                MainActivity.changeFragment(new UnAcceptedRequestsViewFragment());
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public class GetResponsesTask extends AsyncTask<Void, List<Response>, List<Response>>
    {

        @Override
        protected List<Response> doInBackground(Void... voids)
        {
            return ResponseManager.getMyResponses(AuthenticationManager.profileID);
        }

        @Override
        protected void onPostExecute(List<Response> responses)
        {
            MainActivity.showProgress(false,rLMyResponse,pBMyResponse);
            myResponse = responses;
            if (myResponse == null) {
                Log.d("TAG", "There Are No Requests");
                tvNoResponses.setVisibility(View.VISIBLE);
                lvMyResponses.setVisibility(View.GONE);
            }else
            {

                listAdapter = new MyResponsesListAdapter(context,myResponse);
                lvMyResponses.setAdapter(listAdapter);
            }

        }

    }
}
