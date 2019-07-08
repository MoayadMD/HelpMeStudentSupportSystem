package com.tu.gp.helpme.RequestManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tu.gp.helpme.R;
import com.tu.gp.helpme.RequestManager.Request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MyRequestsListAdapter extends ArrayAdapter<Request>
{
    List<Request> requests;
    Context context;
    public MyRequestsListAdapter(@NonNull Context context, List<Request> requests) {
        super(context, R.layout.my_requests_list_view, requests);
        this.requests = requests;
        this.context = context;
        orderMyRequestsList();
    }

    private void orderMyRequestsList()
    {
        Collections.sort(requests);
    }
    @Override
    public View getView(int i, View convertView, ViewGroup parent)
    {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.my_requests_list_view,null);
        TextView tvRequestSpec = view.findViewById(R.id.tvRequestSpec);
        TextView tvRequestDate = view.findViewById(R.id.tvRequestDate);
        TextView tvRequestQuestion = view.findViewById(R.id.tvRequestQuestion);
        ImageView ivStatus = view.findViewById(R.id.ivStatus);
        if(i %2 == 1)
        {
            // Set a background color for ListView regular row/item
            view.setBackgroundColor(Color.parseColor("#ededed"));
        }
        Log.d("Get View","Made it in, no Empty, "+requests.get(i).getRequestStatus());
        tvRequestSpec.setText("Question In: " + requests.get(i).getRequestSpec().getSpecName());
        tvRequestDate.setText("Placed At: " + requests.get(i).getRequestDate());
        tvRequestQuestion.setText("Quistion: " + requests.get(i).getQuestion());
        switch (requests.get(i).getRequestStatus())
        {


            case 0:
                ivStatus.setImageResource(R.drawable.ic_waiting_request);
                break;
            case 1:
                ivStatus.setImageResource(R.drawable.ic_notifications_black_24dp);
                break;
            case 2:
                ivStatus.setImageResource(R.drawable.ic_active_request);
                break;
            case 3:
                ivStatus.setImageResource(R.drawable.ic_survey);
                break;
            case 4:
                ivStatus.setImageResource(R.drawable.ic_finished_request);
                break;
            case 5:
                ivStatus.setImageResource(R.drawable.ic_canceled_request);
                break;
        }
        return view;
    }

}
