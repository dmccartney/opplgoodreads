package com.mleiseca.opplgoodreads;

import android.accounts.*;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mleiseca.opplgoodreads.util.CurrentTime;
import com.mleiseca.opplgoodreads.xml.objects.AuthUser;

import com.google.inject.Inject;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.commons.lang.StringUtils;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


public class StartActivity extends RoboActivity {
//    @InjectView(R.id.title)
//    TextView title;
//
//    @InjectView(R.id.current_time)
//    TextView currentTimeText;
    public static final String TAG = StartActivity.class.getSimpleName();

    @Inject
    private CurrentTime currentTime;

    private Dialog mProgressBar;

    private GoodreadsAPI mGoodreadsApi;

    @InjectView(R.id.home_helpful_text)
    private TextView mHelpfulText;

//
//    @Override
//    public void onDismiss(DialogInterface dialogInterface) {
//        this.cancel(false);
//    }
//
//    @Override
//    public void onCancel(DialogInterface dialogInterface) {
//        this.cancel(false);
//    }
//
//    private void addDialog() {
//        if (context != null && showDialog) {
//            dialog = ProgressDialogBuilder.makeDialog(context);
//
//            //intentionally hard-coded to be non-cancelable
//            dialog.setCancelable(false);
//
//            dialog.setOnDismissListener(this);
//            dialog.setOnCancelListener(this);
//            dialog.show();
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        showProgressBar(false);

        final String oauthDeveloperKey = getString(R.string.oauth_developer_key);
        final String oauthDeveloperSecret = getString(R.string.oauth_developer_secret);
        final String oauthCallbackUrl = getString(R.string.oauth_callback_url);

        //todo: is there a nice way to inject this?
        mGoodreadsApi = new GoodreadsAPI(this, new MyApiEventListener());
        mGoodreadsApi.setOAuthInfo(oauthDeveloperKey, oauthDeveloperSecret, oauthCallbackUrl);


//        title.setText("Hello World");
//        currentTimeText.setText(String.valueOf(currentTime.currentTimeMillis()));

//        AccountManager am = AccountManager.get(this);
//
//        Account[] accounts = am.getAccountsByType("com.goodreads");
//        Bundle options = new Bundle();
//
//        am.getAuthToken(
//                accounts.length > 0 ? accounts[0] : null,                     // Account retrieved using getAccountsByType()
//                "Manage your tasks",            // Auth scope
//                options,                        // Authenticator-specific options
//                this,                           // Your activity
//                new OnTokenAcquired(),          // Callback called when a token is successfully acquired
//                new Handler(new Handler.Callback(){
//                    @Override
//                    public boolean handleMessage(Message message) {
//                        //todo
//                        return false;  //To change body of implemented methods use File | Settings | File Templates.
//                    }
//                }));    // Callback called if an error occurs
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
//        getIntent().getStringExtra()
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
    }

    public CurrentTime getCurrentTime() {
        return currentTime;
    }


    class MyApiEventListener implements GoodreadsAPI.ApiEventListener {

        @Override
        public void OnNeedsCredentials() {
            handleLogin();
        }

    }

    public void handleLogin() {

//        showProgressBar(true);
//
//        mGoodreadsApi.login(new GoodreadsAPI.OAuthLoginCallback() {
//
//            @Override
//            public void onSuccess() {
//                Log.e(TAG, "successful login!");
//                start();
//                showProgressBar(false);
//            }
//
//            @Override
//            public void onError(Throwable tr) {
//                Log.e(TAG, "onError", tr);
//                showProgressBar(false);
//            }
//        });

        try {
            String requestToken = mGoodreadsApi.retrieveRequestToken();

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(requestToken));
            startActivity(i);

        } catch (Exception e) {
            Log.e(TAG, "Problem getting requestToken", e);
        }
    }

    private void showProgressBar(boolean show) {
//        if(mProgressBar == null && show ){
//            mProgressBar= ProgressDialogBuilder.makeDialog(this);
//
//            //intentionally hard-coded to be non-cancelable
//            mProgressBar.setCancelable(false);
//
////            mProgressBar.setOnDismissListener(this);
////            mProgressBar.setOnCancelListener(this);
//            mProgressBar.show();
//        }
//
//        if (mProgressBar != null) {
//            if(show){
//                mProgressBar.show();
//            }else{
//                mProgressBar.dismiss();
//            }
//        }
    }

    private void start() {
        new FetchUserInfoTask().execute();
    }


    private class FetchUserInfoTask extends AsyncTask<Void, Void, AuthUser> {

        @Override
        protected void onPreExecute() {
            showProgressBar(true);
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
