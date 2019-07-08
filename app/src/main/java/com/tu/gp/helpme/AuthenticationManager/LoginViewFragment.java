package com.tu.gp.helpme.AuthenticationManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.CountryCodeToRegionCodeMap;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.DataManager.DataManager;
import com.tu.gp.helpme.MainActivity;
import com.tu.gp.helpme.R;
import com.tu.gp.helpme.RequestManager.RequestManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.navigation.Navigation;

public class LoginViewFragment extends Fragment {

    private EditText etPassword, etEmail,etTest;
    private ProgressBar pBLogin;
    private ScrollView sVLogin;
    private SharedPreferences.Editor editor;
    public static Context context;
    private TextView tvSignUp;
    View focusView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_view, container, false);
        context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.apply();
        /*
        if(!preferences.getString(context.getString(R.string.username),"").equals(""))
        {
            startActivity(new Intent(LoginActivity.context,MainActivity.class));
        }
        */
        tvSignUp = view.findViewById(R.id.tvSignUp);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail.setText(preferences.getString(context.getString(R.string.email),""));
        etPassword.setText(preferences.getString(context.getString(R.string.password),""));
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin(etEmail.getText().toString(),etPassword.getText().toString());
                    return true;
                }
                return false;
            }
        });

        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                attemptLogin(etEmail.getText().toString(),etPassword.getText().toString());
            }
        });
        pBLogin = view.findViewById(R.id.pBLogin);
        sVLogin = view.findViewById(R.id.sVLogin);
        tvSignUp.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.aLoginToSignUp));
       return view;
    }

    private static boolean isPhoneNumberValid(String phoneNumber) {
        //TODO: Replace this with your own logic

        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }

    private void attemptLogin(String email,String password) {
        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        // Store values at the time of the login attempt.


        focusView= null;
        boolean cancel = !validate(email,password);
        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            pBLogin.setVisibility(View.INVISIBLE);
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            MainActivity.showProgressBeforLogin(true,sVLogin,pBLogin);
            UserLoginTask userLoginTask = new UserLoginTask(email,password);
            userLoginTask.execute();
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

    /**
     * Shows the progress UI and hides the login form.
     */
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private boolean validate(String email, String password) {

        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            focusView = etEmail;
            return false;
        } else if (!isEmailValid(email)) {
            etEmail.setError("Enter a valid email");
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
        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private final String password;

        UserLoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            return AuthenticationManager.login(email,password,editor,getActivity());
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            MainActivity.showProgressBeforLogin(false,sVLogin,pBLogin);
            if (success) {
                Navigation.findNavController(getView()).navigate(R.id.aLoginToMain);
                Toast.makeText(getActivity(),"Loggedin successfully!",Toast.LENGTH_SHORT).show();
            } else {
                etPassword.setError(getString(R.string.errorIncorrectLogin));
                etPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            MainActivity.showProgressBeforLogin(false,sVLogin,pBLogin);
        }
    }

}
