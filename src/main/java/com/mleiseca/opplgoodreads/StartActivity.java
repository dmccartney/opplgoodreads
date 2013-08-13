package com.mleiseca.opplgoodreads;

import android.accounts.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.mleiseca.opplgoodreads.util.CurrentTime;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


public class StartActivity extends RoboActivity {
//    @InjectView(R.id.title)
//    TextView title;
//
//    @InjectView(R.id.current_time)
//    TextView currentTimeText;

    @Inject
    private CurrentTime currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
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

    public void onLoginClick(View view){
        Log.d("StartActivity", "got login click");
    }

    public CurrentTime getCurrentTime() {
        return currentTime;
    }



    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Get the result of the operation from the AccountManagerFuture.
            try {
                Bundle bundle = result.getResult();


            //todo:
            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
//            token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                Log.d("StartActivity","Got token: " + bundle.getString(AccountManager.KEY_AUTHTOKEN));
            } catch (Exception e) {
//                title.setText("Authentication error: " + e);
            }

        }
    }
}
