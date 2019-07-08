package com.tu.gp.helpme;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.tu.gp.helpme.AuthenticationManager.AuthenticationManager;
import com.tu.gp.helpme.ProfileManager.MyProfileViewFragment;
import com.tu.gp.helpme.ProfileManager.Profile;
import com.tu.gp.helpme.ProfileManager.ProfileManager;
import com.tu.gp.helpme.RequestManager.MyRequestsViewFragment;
import com.tu.gp.helpme.RequestManager.UnAcceptedRequestsViewFragment;
import com.tu.gp.helpme.ResponsManager.MyResponsesViewFragment;

public class MainActivity extends AppCompatActivity {
    public static Context context;
    private static FragmentManager fragmentManager;
    public static BottomNavigationView nNavView;
    ProgressBar pBMain;
    FrameLayout fLMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        fragmentManager = getSupportFragmentManager();
        nNavView = findViewById(R.id.nNavView);
        pBMain = findViewById(R.id.pBMain);
        fLMain = findViewById(R.id.fLFragmentController);
        showProgress(true,fLMain,pBMain);
        GetProfileTask getProfileTask = new GetProfileTask();
        getProfileTask.execute();
        nNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nMyRequests:
                        changeFragment(new MyRequestsViewFragment());
                        return true;
                    case R.id.nMyProfile:
                        changeFragment(new MyProfileViewFragment());
                        return true;
                    case R.id.nMyResponses:
                        changeFragment(new MyResponsesViewFragment());
                        return true;
                    /*case R.id.nUnAcceptedRequests:
                        changeFragment(new UnAcceptedRequestsViewFragment());
                        return true;*/
                }
                return false;
            }
        });
    }

    public static void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fLFragmentController, fragment, null).commit();
    }

    public static void showProgress(final boolean show, View layot, View progres) {
        if (show) {

            layot.setVisibility(View.GONE);
            progres.setVisibility(View.VISIBLE);
            nNavView.setVisibility(View.INVISIBLE);
        } else {
            layot.setVisibility(View.VISIBLE);
            progres.setVisibility(View.GONE);
            nNavView.setVisibility(View.VISIBLE);
        }

    }

    public static void showProgressBeforLogin(final boolean show, View layot, View progres) {
        if (show) {

            layot.setVisibility(View.GONE);
            progres.setVisibility(View.VISIBLE);
        } else {
            layot.setVisibility(View.VISIBLE);
            progres.setVisibility(View.GONE);
        }

    }

    public class GetProfileTask extends AsyncTask<Void, Void, Profile>
    {

        @Override
        protected Profile doInBackground(Void... voids) {
            return ProfileManager.getProfileByID(AuthenticationManager.profileID);
        }

        @Override
        protected void onPostExecute(Profile profile)
        {
            showProgress(false,fLMain,pBMain);
            if(profile.getType())
            {
                nNavView.setSelectedItemId(R.id.nMyRequests);
                changeFragment(new MyRequestsViewFragment());
            }else
            {
                nNavView.getMenu().removeItem(R.id.nMyRequests);
                nNavView.setSelectedItemId(R.id.nMyResponses);
                changeFragment(new MyResponsesViewFragment());
            }
        }
    }

}
