package com.tu.gp.helpme.ProfileManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.CountryCodeToRegionCodeMap;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.AuthenticationManager.LoginViewFragment;
import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.ProfileManager;
import com.tu.gp.helpme.ProfileManager.Spec;
import com.tu.gp.helpme.R;
import com.tu.gp.helpme.RequestManager.RequestDetailsViewFragment;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.navigation.Navigation;


public class SignUpViewFragment extends Fragment {

    public SignUpViewFragment() {
        // Required empty public constructor
    }



    private EditText etPhoneNumber,etFullName,etEmail,etPassword,etConfermPassword;
    RadioGroup radioGroup;
    private ProgressBar pBSignUp;
    private ScrollView sVSignUp;
    TextView tvSelectedSpecs;
    private Button btnSelectedSpecs,btnSignUp;
    private List<Spec> selectedSpecs;
    View focusView;
    FloatingActionButton btnNewSpec;
    List<Spec> allSpecs;
    Context context;
    static Context staticContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        staticContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_up_view, container, false);
        etFullName = view.findViewById(R.id.etFullName);
        etPassword = view.findViewById(R.id.etPassword);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etEmail = view.findViewById(R.id.etEmail);
        pBSignUp = view.findViewById(R.id.pBSignUp);
        sVSignUp= view.findViewById(R.id.sVSignUp);
        btnSelectedSpecs = view.findViewById(R.id.btnSelectSpecs);
        tvSelectedSpecs = view.findViewById(R.id.tvSelectedSpecs);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        etConfermPassword = view.findViewById(R.id.etConfermPassword);

        selectedSpecs = new LinkedList<>();
        radioGroup = view.findViewById(R.id.rGType);
        btnNewSpec = view.findViewById(R.id.btnNewSpec);
        MainActivity.showProgressBeforLogin(true,sVSignUp,pBSignUp);
        GetSpecsTask getSpecsTask = new GetSpecsTask();
        getSpecsTask.execute();
        btnSelectedSpecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSpecsTask getSpecsTask = new GetSpecsTask();
                getSpecsTask.execute();
               showMultiSelectionDialog(allSpecs);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                MainActivity.showProgressBeforLogin(true,sVSignUp,pBSignUp);
                try {
                    attmepmtSignUp();
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnNewSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showAddNewSpecDialog();
            }
        });
        return view;
    }


    private void showMultiSelectionDialog(final List<Spec> specs)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.multiSelectionDialogTitle);
        String[] specsNames = new String[specs.size()];
        final boolean[] checkedSpecs = new boolean[specs.size()];
        for(int i = 0 ; i <specs.size();i++)
        {
            specsNames[i] = specs.get(i).getSpecName();
        }

        builder.setMultiChoiceItems(specsNames, checkedSpecs, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked)
            {
                checkedSpecs[which] = isChecked;
            }
        });
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvSelectedSpecs.setText("");
                for(int i = 0 ; i<checkedSpecs.length;i++)
                {
                    if(checkedSpecs[i])
                    {
                        tvSelectedSpecs.setVisibility(View.VISIBLE);
                        selectedSpecs.add(specs.get(i));
                        tvSelectedSpecs.setText(tvSelectedSpecs.getText().toString()+"("+specs.get(i)+")");
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        MainActivity.showProgressBeforLogin(false,sVSignUp,pBSignUp);
        dialog.show();

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
                MainActivity.showProgressBeforLogin(true,sVSignUp,pBSignUp);
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
                    MainActivity.showProgressBeforLogin(false,sVSignUp,pBSignUp);
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

    private void attmepmtSignUp() throws NumberParseException {
        boolean cancel = false;
        String password = etPassword.getText().toString();
        String passwordConfermation = etConfermPassword.getText().toString();
        String fullName = etFullName.getText().toString();
        String email = etEmail.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();

        RadioButton rbStudent = getView().findViewById(R.id.rBStudent);
        boolean type;
        if(rbStudent.isChecked())
        {
            type = true;
        }else
        {
            type = false;
        }
        focusView= null;
        cancel = !validate(email,password,fullName,phoneNumber,passwordConfermation);

        if(cancel)
        {
            MainActivity.showProgressBeforLogin(false,sVSignUp,pBSignUp);
            if(focusView != null)
            {
                focusView.requestFocus();
            }
        }else
        {
            MainActivity.showProgressBeforLogin(false,sVSignUp,pBSignUp);
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber,"");
            phoneNumber = phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            Log.d("Phone Number",phoneNumber);
            SignUpTask signUpTask = new SignUpTask();
            signUpTask.execute(password,email,type+"",fullName,phoneNumber);
        }
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
    private boolean validate(String email, String password,String fullName,String phoneNumber,String confermPassword) {

        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);
        etFullName.setError(null);
        etPhoneNumber.setError(null);
        etConfermPassword.setError(null);


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

        if (TextUtils.isEmpty(confermPassword)) {
            etConfermPassword.setError("Please Re-type your password");
            focusView = etConfermPassword;
            return false;
        } else if (!password.equals(confermPassword)) {
            etConfermPassword.setError("Passwords Don't Match");
            focusView = etConfermPassword;
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

        if(selectedSpecs.isEmpty())
        {
            Toast.makeText(context,"Please Selecet At Least One Specialization",Toast.LENGTH_LONG).show();
            focusView = btnSelectedSpecs;
            return false;
        }
        return true;
    }

    public class SignUpTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... profile)
        {
            return ProfileManager.createProfile(profile[0],profile[1],profile[2],profile[3],profile[4],selectedSpecs);
        }

        @Override
        protected void onPostExecute(String result) {
            MainActivity.showProgressBeforLogin(false,sVSignUp,pBSignUp);
            if(result == null)
            {
                Toast.makeText(getContext(),"Signed Up Successfuly",Toast.LENGTH_LONG).show();
                //MainActivity.changeFragment(new LoginViewFragment());
                Navigation.findNavController(getView()).navigate(R.id.aSignUpToLogin);
            }else
            {
                Log.d("SignUpTask","Error: "+result);
                Toast.makeText(getContext(),"Error: "+result,Toast.LENGTH_LONG).show();
            }
        }
    }

    public class GetSpecsTask extends AsyncTask<Void, List<Spec>, List<Spec>> {
        @Override
        protected List<Spec> doInBackground(Void... voids)
        {
            return ProfileManager.getAllSpecs();
        }

        @Override
        protected void onPostExecute(List<Spec> specs)
        {
            MainActivity.showProgressBeforLogin(false,sVSignUp,pBSignUp);
            allSpecs = specs;
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
