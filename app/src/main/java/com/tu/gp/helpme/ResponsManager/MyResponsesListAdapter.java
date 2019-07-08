package com.tu.gp.helpme.ResponsManager;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class MyResponsesListAdapter  extends ArrayAdapter<Response>
{
    List<Response> responses;
    Context context;
    public MyResponsesListAdapter(@NonNull Context context, List<Response> responses) {
        super(context, R.layout.my_responses_list_view, responses);
        this.responses = responses;
        this.context = context;
        orderMyResponsesList();
    }

    private void orderMyResponsesList()
    {
        Collections.sort(responses, new Comparator<Response>() {
            @Override
            public int compare(Response o1, Response o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = null;
                Date date2 = null;
                try {
                    date2 = sdf.parse(o2.getResponseDate());
                    date1 = sdf.parse(o1.getResponseDate());
                    Log.d("ResponseCompare",date2.compareTo(date1)+" Is the result");
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d("ResponseCompare","Catch");
                }
                return 0;
            }
        });
    }
    @Override
    public View getView(int i, View convertView, ViewGroup parent)
    {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.my_responses_list_view,null);
      /*  TextView tvResponseSpec = view.findViewById(R.id.tvResponseSpec);
        TextView tvResponseDate = view.findViewById(R.id.tvResponseDate);
        TextView tvRequesterPhoneNumber = view.findViewById(R.id.tvRequesterPhoneNumber);
        ImageView ivStatus = view.findViewById(R.id.ivStatus);
        if(i%2==0)
        {
            view.setBackgroundColor(view.getResources().getColor(R.color.dark));
        }

        Log.d("Get View","Made it in, no Empty, "+responses.get(i).getRequestStatus()+", ID "+responses.get(i).getRequestID());
        tvResponseSpec.setText("Specialization:" + responses.get(i).getRequestSpec().getSpecName());
        tvResponseDate.setText("Responsed At: " + responses.get(i).getResponseDate());
        tvRequesterPhoneNumber.setText("Phone Number: " + responses.get(i).getServiceRequester().getPhoneNumber());
        ivStatus.setVisibility(View.GONE);
        /*
        switch (responses.get(i).getRequestStatus()) {

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
        */
        return view;
    }
}
