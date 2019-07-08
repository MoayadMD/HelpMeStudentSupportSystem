package com.tu.gp.helpme.ProfileManager;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.R;

import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileViewFragment extends Fragment {


    public MyProfileViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    Context context;
    private EditText etPhoneNumber,etFullName,etEmail,etSelectedSpecs,etPassword;
    RadioButton rbStudent,rBTecher;
    RadioGroup radioGroup;
    private ProgressBar pBMyProfile;
    private ScrollView sVMyProfile;
    Button btnModifyProfile,btnModifySpecs,btnAddNewSpec;
    List<Spec> mySpecs,allSpecs;
    View focusView;
    GetProfileTask getProfileTask;
    GetProfileSpecs getProfileSpecs;
    GetSpecsTask getSpecsTask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile_view, container, false);
        etFullName = view.findViewById(R.id.etFullName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        rbStudent = view.findViewById(R.id.rBStudent);
        rBTecher = view.findViewById(R.id.rBTecher);
        pBMyProfile = view.findViewById(R.id.pBMyProfile);
        sVMyProfile= view.findViewById(R.id.sVMyProfile);
        etSelectedSpecs= view.findViewById(R.id.etSelectedSpecs);
        btnModifyProfile = view.findViewById(R.id.btnModifyProfile);
        btnModifySpecs = view.findViewById(R.id.btnModifySpecs);
        btnAddNewSpec = view.findViewById(R.id.btnAddNewSpec);
        mySpecs = new LinkedList<>();
        MainActivity.showProgress(true,sVMyProfile,pBMyProfile);
        btnModifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                MainActivity.showProgress(true,sVMyProfile,pBMyProfile);
                modifyProfile();
            }
        });

        btnModifySpecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                showMultiSelectionDialog(allSpecs);
            }
        });
        btnAddNewSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewSpecDialog();
            }
        });
        MainActivity.showProgress(true,sVMyProfile,pBMyProfile);
        getProfileTask = new GetProfileTask();
        getProfileSpecs = new GetProfileSpecs();
        getSpecsTask = new GetSpecsTask();

        getSpecsTask.execute();
        getProfileTask.execute();
        getProfileSpecs.execute();
        return view;
    }

    private void chickLiveThreads()
    {
        if(getProfileSpecs.getStatus() == AsyncTask.Status.FINISHED
        && getProfileTask.getStatus() == AsyncTask.Status.FINISHED
        && getSpecsTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            MainActivity.showProgress(false,sVMyProfile,pBMyProfile);
        }
    }
    EditText etNewSpecName;
    private void showAddNewSpecDialog()
    {
        final AlertDialog.Builder addNewSpecBuilder = new AlertDialog.Builder(getContext());
        final View addNewSpecView = getLayoutInflater().inflate(R.layout.dialog_new_spec,null);
        etNewSpecName = addNewSpecView.findViewById(R.id.etNewSpecName);

        addNewSpecBuilder.setPositiveButton("Conferm", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MainActivity.showProgressBeforLogin(true,sVMyProfile,pBMyProfile);
                Boolean existSpecFlag = false;
                for(Spec spec:allSpecs)
                {
                    Log.d("allSpecs: ",spec.getSpecName()+" Spec: "+etNewSpecName.getText().toString());
                    if(spec.getSpecName().equalsIgnoreCase(etNewSpecName.getText().toString()))
                    {
                        existSpecFlag = true;
                        break;
                    }
                }
                if(!existSpecFlag)
                {
                    AddNewSpecTask addNewSpecTask = new AddNewSpecTask();
                    addNewSpecTask.execute(etNewSpecName.getText().toString());
                }else
                {
                    Toast.makeText(context,"This Specialization already exists!",Toast.LENGTH_LONG).show();
                    MainActivity.showProgressBeforLogin(false,sVMyProfile,pBMyProfile);
                }
            }
        });


        addNewSpecBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getContext(),"Canceled",Toast.LENGTH_LONG).show();
            }
        });
        addNewSpecBuilder.setView(addNewSpecView);
        final AlertDialog addNewSpecDialog = addNewSpecBuilder.create();
        addNewSpecDialog.show();
        addNewSpecDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        etNewSpecName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!s.toString().isEmpty()) {
                    addNewSpecDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else
                {
                    addNewSpecDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    etNewSpecName.setError(getString(R.string.errorFieldRequired));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()) {
                    addNewSpecDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else
                {
                    addNewSpecDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    etNewSpecName.setError(getString(R.string.errorFieldRequired));
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(!s.toString().isEmpty()) {
                    addNewSpecDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else
                {
                    addNewSpecDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    etNewSpecName.setError(getString(R.string.errorFieldRequired));
                }

            }
        });
    }

    private boolean validate(String email, String password,String fullName,String phoneNumber) {

        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);
        etFullName.setError(null);
        etPhoneNumber.setError(null);


        if(TextUtils.isEmpty(fullName))
        {
            etFullName.setError("Full name is required");
            focusView = etFullName;
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            focusView = etEmail;
            return false;
        } else if (!isEmailValid(email)) {
            etEmail.setError("Please enter a valid email");
            focusView = etEmail;
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            focusView = etPassword;
            return false;
        } else if (!isPasswordValid(password)) {
            etPassword.setError("Password must contain at least 6 characters");
            focusView = etPassword;
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Phone Number is required");
            focusView = etPhoneNumber;
            return false;
        } else if (!isPhoneNumberValid(phoneNumber)) {
            etPhoneNumber.setError("Please enter a valid Phone Number e.g(+966501234567");
            focusView = etPhoneNumber;
            return false;
        }
        return true;
    }

    private static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }
    private static boolean isPhoneNumberValid(String phoneNumber)
    {
        //TODO: Replace this with your own logic
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try
        {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber,"");
            Log.d("PhoneNumberChick",phoneNumber+" is "+ phoneUtil.isValidNumber(number));
            return phoneUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void modifyProfile()
    {
        String email = etEmail.getText().toString();
        String password= etPassword.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String fullName = etFullName.getText().toString();

        boolean type;
        if(rbStudent.isChecked())
        {
            type = true;
        }else
        {
            type = false;
        }
        if(validate(email,password,fullName,phoneNumber))
        {
            ModifyProfileTask modifyProfileTask = new ModifyProfileTask();
            modifyProfileTask.execute(fullName, password, email, phoneNumber, type + "");
        }else
        {
            MainActivity.showProgress(false,sVMyProfile,pBMyProfile);
            focusView.requestFocus();
            Toast.makeText(context, "Faild", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMultiSelectionDialog(final List<Spec> specs)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.multiSelectionDialogTitle);
        String[] specsNames = new String[specs.size()];
        final boolean[] checkedSpecs = new boolean[specs.size()];

        for(int i = 0 ; i <specs.size();i++)
        {
            specsNames[i] = specs.get(i).getSpecName();
        }

        for(Spec spec:mySpecs)
        {
            for(int i = 0 ; i <specsNames.length;i++)
            {
                if(spec.getSpecName().equalsIgnoreCase(specsNames[i]))
                {
                    checkedSpecs[i] = true;
                }
            }
        }
        builder.setMultiChoiceItems(specsNames, checkedSpecs, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked)
            {
                checkedSpecs[which] = isChecked;
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etSelectedSpecs.setText("");
                mySpecs = new LinkedList<>();
                for(int i = 0 ; i<checkedSpecs.length;i++)
                {
                    if(checkedSpecs[i])
                    {
                        etSelectedSpecs.setVisibility(View.VISIBLE);
                        mySpecs.add(specs.get(i));
                        etSelectedSpecs.setText(etSelectedSpecs.getText().toString()+"\n("+specs.get(i)+")");
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class ModifyProfileTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            Log.d("ModifyProfile","InBack");
            return ProfileManager.modifyProfile(strings[0],strings[1],strings[2],strings[3]
                    ,Boolean.parseBoolean(strings[4]),mySpecs,AuthenticationManager.profileID);
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.d("ModifyProfile",result+"");
            MainActivity.showProgress(false, sVMyProfile, pBMyProfile);
            if(TextUtils.isEmpty(result) || result.contains("null")) {
                Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                MainActivity.changeFragment(new MyProfileViewFragment());
                MainActivity.nNavView.setSelectedItemId(R.id.nMyProfile);
            }else
            {
                Toast.makeText(context, "Faield: "+result, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetProfileTask extends AsyncTask<Void,Void,Profile>
    {

        @Override
        protected Profile doInBackground(Void... voids) {
            return ProfileManager.getProfileByID(AuthenticationManager.profileID);
        }

        @Override
        protected void onPostExecute(Profile profile)
        {
            chickLiveThreads();
            etEmail.setText(profile.getEmail());
            etFullName.setText(profile.getFullName());
            etPhoneNumber.setText(profile.getPhoneNumber());
            etPassword.setText(profile.getPassword());
            rbStudent.setChecked(profile.getType());
            rBTecher.setChecked(!profile.getType());
        }
    }

    public class GetSpecsTask extends AsyncTask<Void, List<Spec>, List<Spec>>
    {
        @Override
        protected List<Spec> doInBackground(Void... voids)
        {
            return DataManager.getAllSpecs();
        }

        @Override
        protected void onPostExecute(List<Spec> specs)
        {
            chickLiveThreads();
            allSpecs = specs;
        }

    }

    public class GetProfileSpecs extends AsyncTask<Void, List<Spec>, List<Spec>>
    {

        @Override
        protected List<Spec> doInBackground(Void... voids)
        {
            return ProfileManager.getProfileSpec(AuthenticationManager.profileID);
        }

        @Override
        protected void onPostExecute(List<Spec> specs)
        {
            Log.d("GetProfileSpecs","POST");
            MainActivity.showProgress(false,sVMyProfile,pBMyProfile);
            mySpecs = specs;
            for(int i = 0 ; i <mySpecs.size();i++)
            {
                Log.d("MySpecs",mySpecs.get(i).getSpecName());
                etSelectedSpecs.setText(etSelectedSpecs.getText().toString()
                        + "\n(" + mySpecs.get(i).getSpecName()+")");
            }
        }
    }

    public class AddNewSpecTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings) {
            return ProfileManager.createSpec(strings[0]);
        }

        @Override
        protected void onPostExecute(String s)
        {
            MainActivity.showProgress(false,sVMyProfile,pBMyProfile);
            if(s == null || s.isEmpty() || s.contains("null"))
            {
                Toast.makeText(context,"Added New Sepcialization Successfully!",Toast.LENGTH_SHORT).show();
                GetSpecsTask getSpecsTask = new GetSpecsTask();
                getSpecsTask.execute();
            }else
            {
                Log.d("AddNewSpec","Failed, "+s);
                Toast.makeText(context,"Failed!, "+s,Toast.LENGTH_SHORT).show();
            }
        }
    }


}
