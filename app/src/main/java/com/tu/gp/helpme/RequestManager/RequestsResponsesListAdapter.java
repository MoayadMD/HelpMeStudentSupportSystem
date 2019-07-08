package com.tu.gp.helpme.RequestManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tu.gp.helpme.R;
import com.tu.gp.helpme.ResponsManager.Response;

import java.util.Collections;
import java.util.List;

public class RequestsResponsesListAdapter extends ArrayAdapter<Response>
{
    List<Response> responses;
    Context context;
    public RequestsResponsesListAdapter(@NonNull Context context, List<Response> responses) {
        super(context, R.layout.request_responses_list_view, responses);
        this.responses = responses;
        this.context = context;
        orderMyResponsesList();
    }

    private void orderMyResponsesList()
    {
        Collections.sort(responses);
    }
    @Override
    public View getView(int i, View convertView, ViewGroup parent)
    {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.request_responses_list_view,null);
        EditText etResponser = view.findViewById(R.id.etResponser);
        CheckBox cbTeacher = view.findViewById(R.id.cbTeacher);
        CheckBox cbStudent = view.findViewById(R.id.cbStudent);
        TextView tvRating= view.findViewById(R.id.tvRating);
        Log.d("Get View","Made it in, no Empty, "+responses.get(i).getRequestStatus()+", ID "+responses.get(i).getRequestID());

        tvRating.setText(responses.get(i).getServiceProvider().getRating()+"");
        //tvRequesterPhoneNumber.setText("Phone Number: " + responses.get(i).get);
        etResponser.setText(responses.get(i).getServiceProvider().getFullName());
        cbStudent.setChecked(responses.get(i).getServiceProvider().getType());
        cbTeacher.setChecked(!responses.get(i).getServiceProvider().getType());
        return view;
    }
}
