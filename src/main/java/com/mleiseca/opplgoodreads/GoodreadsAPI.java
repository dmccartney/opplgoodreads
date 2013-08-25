package com.mleiseca.opplgoodreads;

import com.google.inject.Inject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.mleiseca.opplgoodreads.xml.objects.AuthUser;
import com.mleiseca.opplgoodreads.xml.objects.Review;
import com.mleiseca.opplgoodreads.xml.responses.AuthUserResponse;
import com.mleiseca.opplgoodreads.xml.responses.ReviewsListResponse;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/14/13 Time: 11:49 PM To change this template use File | Settings | File Templates.
 */
public class GoodreadsAPI {

    private static final String KEY = "key";

    private static final String API_URL = "https://www.goodreads.com/";

    private static final String AUTHORIZATION_WEBSITE_URL = "http://www.goodreads.com/oauth/authorize?mobile=1";
    private static final String ACCESS_TOKEN_ENPOINT_URL = "http://www.goodreads.com/oauth/access_token";
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "http://www.goodreads.com/oauth/request_token";

    private static final String SHARED_PREF_FILENAME = GoodreadsAPI.class.getPackage().getName();
    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String USER_SECRET = "USER_SECRET";
    public static final String REQUEST_TOKEN = "REQUEST_TOKEN";
    public static final String REQUEST_SECRET = "REQUEST_SECRET";

    public static final String TAG = GoodreadsAPI.class.getSimpleName();

    private Context mContext;
    HttpClient httpClient ;

    private CommonsHttpOAuthConsumer mConsumer;
    private CommonsHttpOAuthProvider mProvider;
    private String mOAuthDeveloperKey;
    private String mOAuthCallbackUrl;

    private boolean mIsLoggedIn;

    @Inject
    public GoodreadsAPI(Context context) {
        this.httpClient = new DefaultHttpClient();
        this.mContext = context;


        final String oauthDeveloperKey = mContext.getString(R.string.oauth_developer_key);
        final String oauthDeveloperSecret = mContext.getString(R.string.oauth_developer_secret);
        final String oauthCallbackUrl = mContext.getString(R.string.oauth_callback_url);

        setOAuthInfo(oauthDeveloperKey, oauthDeveloperSecret, oauthCallbackUrl);
    }

    private void setOAuthInfo(String oAuthDeveloperKey, String oAuthDeveloperSecret, String oAuthCallbackUrl) {
        mOAuthDeveloperKey = oAuthDeveloperKey;
        mOAuthCallbackUrl = oAuthCallbackUrl;

        if (TextUtils.isEmpty(oAuthDeveloperSecret) || TextUtils.isEmpty(oAuthDeveloperSecret)
            || TextUtils.isEmpty(oAuthCallbackUrl)) {
            String exception = "None may be empty: oAuthDeveloperKey, oAuthDeveloperSecret, oAuthCallbackUrl.";
            Log.e(TAG, exception);
            throw new RuntimeException(exception);
        }

        mConsumer = new CommonsHttpOAuthConsumer(mOAuthDeveloperKey, oAuthDeveloperSecret);
        mProvider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL,
                                                 ACCESS_TOKEN_ENPOINT_URL,
                                                 AUTHORIZATION_WEBSITE_URL);
        mProvider.setOAuth10a(true);
        loadTokens();


    }

    public void loadTokens() {
        // Check if we have a token
        SharedPreferences sp = getSharedPrefs();

        String token = sp.getString(USER_TOKEN, null);
        String tokenSecret = sp.getString(USER_SECRET, null);

        Log.d(TAG, "found token: " + token);
        Log.d(TAG, "found tokenSecret: " + tokenSecret);
        if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(tokenSecret)) {
            mConsumer.setTokenWithSecret(token, tokenSecret);
            mIsLoggedIn = true;
        } else {
            mIsLoggedIn = false;
        }
    }

    private String request(String service) throws Exception {
        return request(service, null);
    }

    private String request(String service, Map<String, String> params) throws Exception {
        String output = null;

        // Create the request URL
        StringBuilder url = new StringBuilder(API_URL).append(service);

        // Create the set of request parameters, starting with developer key
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair(KEY, mOAuthDeveloperKey));

        // If params have been passed in, add them to the request params now
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                String key = param.getKey();
                String value = param.getValue();
                if (StringUtils.isNotEmpty(key) && value != null) {
                    requestParams.add(new BasicNameValuePair(key, value));
                }
            }
        }

        // Add the encoded params to the request URL
        if (requestParams != null && !requestParams.isEmpty()) {
            if (url.charAt(url.length() - 1) != '?') {
                url.append('?');
            }
            url.append(URLEncodedUtils.format(requestParams, "UTF-8"));
        }

        HttpGet get = new HttpGet(url.toString());

        //this should only happen...? when?
        mConsumer.sign(get);
        HttpResponse response = httpClient.execute(get);
        InputStream is = response.getEntity().getContent();
        output = IOUtils.toString(is, "UTF-8");

        final int statusCode = response.getStatusLine().getStatusCode();
        Log.d(TAG, "status code = " + statusCode);
        // Log.d(TAG, "output = " + output);

        if (statusCode == 401) {
            clearAuthInformation();
        }

        return output;
    }

    public String retrieveRequestToken()
        throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        String s = mProvider.retrieveRequestToken(mConsumer, mOAuthCallbackUrl);
        saveRequestInformation(mConsumer.getToken(), mConsumer.getTokenSecret());
        saveAuthInformation(mConsumer.getToken(), mConsumer.getTokenSecret());
        return s;
    }

    public void retrieveAccessToken(String param)  {
        try {
            mProvider.retrieveAccessToken(mConsumer, param);
            String token = mConsumer.getToken();
            String tokenSecret = mConsumer.getTokenSecret();
            mConsumer.setTokenWithSecret(token, tokenSecret);

            saveAuthInformation(token, tokenSecret);
            clearRequestInformation();

        } catch (Exception e) {
            Log.e(TAG, "Exception while retrieving access token.", e);
        }
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    private void saveAuthInformation(String token, String secret) {
        SharedPreferences.Editor editor = getSharedPrefs().edit();

        Log.d(TAG, "saving auth token:  " + token);
        Log.d(TAG, "saving auth secret: " + secret);

        editor.putString(USER_TOKEN, token);
        editor.putString(USER_SECRET, secret);

        editor.commit();
    }

    private void clearAuthInformation() {
        SharedPreferences.Editor editor = getSharedPrefs().edit();

        Log.d(TAG, "clearingAuthInformation");
        editor.remove(USER_TOKEN);
        editor.remove(USER_SECRET);

        editor.commit();
    }

    private void saveRequestInformation(String token, String secret) {
        SharedPreferences.Editor editor = getSharedPrefs().edit();

        Log.d(TAG, "saving request token:  " + token);
        Log.d(TAG, "saving request secret: " + secret);

        editor.putString(REQUEST_TOKEN, token);
        editor.putString(REQUEST_SECRET, secret);

        editor.commit();
    }

    private void clearRequestInformation() {
        SharedPreferences.Editor editor = getSharedPrefs().edit();

        editor.remove(REQUEST_TOKEN);
        editor.remove(REQUEST_SECRET);

        editor.commit();
    }

    private SharedPreferences getSharedPrefs() {
        return mContext.getSharedPreferences(SHARED_PREF_FILENAME, Activity.MODE_PRIVATE);
    }

    public List<Review> retrieveBooksOnShelf(String shelfName){
        List<Review> ret = null;

        try {
            Map<String, String> params = new HashMap<String, String>();
            String id = getAuthUserInfo().getId();
            params.put("id", id);
            params.put("v", "2");
            params.put("per_page", "50");
            if(shelfName != null){
                params.put("shelf",shelfName);
            }
            String output = request("review/list/" +id + ".xml" , params);

            Serializer serializer = new Persister();

            ReviewsListResponse response = serializer.read(ReviewsListResponse.class, output);
            if (response != null) {
                ret = response.getReviews();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        return ret;

    }

    /**
     * Get id of user who authorized OAuth.
     *
     * @return
     */
    public AuthUser getAuthUserInfo() {
        AuthUser ret = null;

        try {
            String output = request("api/auth_user");

            Serializer serializer = new Persister();

            AuthUserResponse response = serializer.read(AuthUserResponse.class, output);
            if (response != null) {
                ret = response.getAuthUser();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        return ret;
    }
}
