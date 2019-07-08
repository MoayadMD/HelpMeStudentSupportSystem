package com.tu.gp.helpme.RequestManager;


import android.content.Context;
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

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnAcceptedRequestsViewFragment extends Fragment {

    ListView lvUnAcceptedRequests;
    List<Request> unAcceptedRequests;
    public static Request selectedRequest;
    private ProgressBar pBUnAcceptedRequests;
    private RelativeLayout rLUnAcceptedRequests;
    MyRequestsListAdapter listAdapter;
    TextView tvNoUnAcceptedRequests;
    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_un_accepted_requests_view, container, false);
        lvUnAcceptedRequests = view.findViewById(R.id.lvUnAcceptedRequests);
        pBUnAcceptedRequests = view.findViewById(R.id.pBUnAcceptedRequests);
        rLUnAcceptedRequests = view.findViewById(R.id.rLUnAcceptedRequests);
        tvNoUnAcceptedRequests = view.findViewById(R.id.tvNoUnAcceptedRequests);
        MainActivity.showProgress(true,rLUnAcceptedRequests,pBUnAcceptedRequests);
        GetUnAcceptedRequestsTask getUnAcceptedRequestsTask = new GetUnAcceptedRequestsTask();
        getUnAcceptedRequestsTask.execute();
        //allSpecslist = new LinkedList<>();

        lvUnAcceptedRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRequest = unAcceptedRequests.get(position);
                MainActivity.changeFragment(new UnAcceptedRequestDetailsViewFragment());
            }
        });
        return view;
    }

    public class GetUnAcceptedRequestsTask extends AsyncTask<Void, List<Request>, List<Request>>
    {

        @Override
        protected List<Request> doInBackground(Void... voids)
        {
            Log.d("unAccReq", "There Are No Requests"+" "+AuthenticationManager.profileID);
            return RequestManager.getUnAcceptedRequests(AuthenticationManager.profileID);
        }

        @Override
        protected void onPostExecute(List<Request> requests)
        {
            MainActivity.showProgress(false,rLUnAcceptedRequests,pBUnAcceptedRequests);
            unAcceptedRequests = requests;
            if (unAcceptedRequests == null || unAcceptedRequests.isEmpty()) {
                Log.d("TAG", "There Are No Requests");
                tvNoUnAcceptedRequests.setVisibility(View.VISIBLE);
                lvUnAcceptedRequests.setVisibility(View.GONE);
            }else
            {

                listAdapter = new MyRequestsListAdapter(context,unAcceptedRequests);
                lvUnAcceptedRequests.setAdapter(listAdapter);
            }

        }

    }


}
