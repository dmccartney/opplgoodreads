package com.mleiseca.opplgoodreads;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.mleiseca.opplgoodreads.util.CurrentTime;
import com.mleiseca.opplgoodreads.xml.objects.AuthUser;

import com.google.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


public class StartActivity extends RoboActivity {
    public static final String TAG = StartActivity.class.getSimpleName();

    @Inject
    private CurrentTime currentTime;

    @Inject
    private GoodreadsAPI mGoodreadsApi;

    @InjectView(R.id.home_helpful_text)
    private TextView mHelpfulText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    @Override protected void onStart() {
        super.onStart();    //To change body of overridden methods use File | Settings | File Templates.
        Uri data = getIntent().getData();
        if(data != null){
            mGoodreadsApi.loadTokens();
            mGoodreadsApi.retrieveAccessToken(data.toString());
        }


        if(mGoodreadsApi.isLoggedIn()){
            mHelpfulText.setText("You are logged in!");
        }else{
            mHelpfulText.setText("You are NOT logged in!");
        }
    }

    public void onLoginClick(View view){

        if (mGoodreadsApi.isLoggedIn()) {
            Log.d(TAG, "isLoggedIn");
            start();
        } else {
            Log.d(TAG, "!isLoggedIn - calling login()");
            handleLogin();
        }

        Log.d("StartActivity", "got login click");
    }

    public void onViewShelfClick(View view){
        Log.d("StartActivity", "got view shelf click");
        Intent i = new Intent(this, DisplayShelfActivity.class);
        startActivity(i);
    }

    public CurrentTime getCurrentTime() {
        return currentTime;
    }



    public void handleLogin() {

        try {
            String requestToken = mGoodreadsApi.retrieveRequestToken();

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(requestToken));
            startActivity(i);

        } catch (Exception e) {
            Log.e(TAG, "Problem getting requestToken", e);
        }
    }

    private void start() {
        new FetchUserInfoTask().execute();
    }


    private class FetchUserInfoTask extends AsyncTask<Void, Void, AuthUser> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected AuthUser doInBackground(Void... params) {
            return mGoodreadsApi.getAuthUserInfo();
        }

        @Override
        protected void onPostExecute(AuthUser authUser) {

            if (authUser != null) {
                Log.i(TAG, "User Name: " + authUser.getName());
            }
        }

    }




}
