package com.mleiseca.opplgoodreads;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.mleiseca.opplgoodreads.xml.objects.AuthUser;
import com.mleiseca.opplgoodreads.xml.objects.Author;
import com.mleiseca.opplgoodreads.xml.objects.Review;
import com.mleiseca.opplgoodreads.xml.responses.AuthUserResponse;
import com.mleiseca.opplgoodreads.xml.responses.AuthorResponse;
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
import java.util.ResourceBundle;

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

    private Activity mActivity;
    private CommonsHttpOAuthConsumer mConsumer;
    private CommonsHttpOAuthProvider mProvider;
    private String mToken;
    private String mTokenSecret;
    private String mOAuthDeveloperKey;
    private String mOAuthDeveloperSecret;
    private String mOAuthCallbackUrl;
    private OAuthLoginDialogType mOAuthLoginDialogType;

    private boolean mIsLoggedIn;

    private ApiEventListener mListener;

    public static enum OAuthLoginDialogType {
        FULLSCREEN, DIALOG
    }

    public interface OAuthLoginCallback {
        void onSuccess();

        void onError(Throwable tr);
    }

    public interface ApiEventListener {

        void OnNeedsCredentials();
    }

    public GoodreadsAPI(Activity activity, ApiEventListener listener) {
        mActivity = activity;
        mListener = listener;

        // Set a default, can be changed by user
//        mOAuthLoginDialogType = UIUtils.isSmallestWidthGreaterThan600dp(activity) ? OAuthLoginDialogType.DIALOG
//                                                                                  : OAuthLoginDialogType.FULLSCREEN;

        mOAuthLoginDialogType = OAuthLoginDialogType.DIALOG;


        final String oauthDeveloperKey = activity.getString(R.string.oauth_developer_key);
        final String oauthDeveloperSecret = activity.getString(R.string.oauth_developer_secret);
        final String oauthCallbackUrl = activity.getString(R.string.oauth_callback_url);

        setOAuthInfo(oauthDeveloperKey, oauthDeveloperSecret, oauthCallbackUrl);
    }

    private void setOAuthInfo(String oAuthDeveloperKey, String oAuthDeveloperSecret, String oAuthCallbackUrl) {
        mOAuthDeveloperKey = oAuthDeveloperKey;
        mOAuthDeveloperSecret = oAuthDeveloperSecret;
        mOAuthCallbackUrl = oAuthCallbackUrl;

        if (TextUtils.isEmpty(mOAuthDeveloperSecret) || TextUtils.isEmpty(mOAuthDeveloperSecret)
            || TextUtils.isEmpty(mOAuthCallbackUrl)) {
            String exception = "None may be empty: oAuthDeveloperKey, oAuthDeveloperSecret, oAuthCallbackUrl.";
            Log.e(TAG, exception);
            throw new RuntimeException(exception);
        }

        mConsumer = new CommonsHttpOAuthConsumer(mOAuthDeveloperKey, mOAuthDeveloperSecret);
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

    public void setOAuthLoginDialogType(OAuthLoginDialogType oAuthLoginDialogType) {
        mOAuthLoginDialogType = oAuthLoginDialogType;
    }

    public void login(OAuthLoginCallback callback) {
        new RetrieveRequestTokenTask(callback).execute();
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
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(get);
        InputStream is = response.getEntity().getContent();
        output = IOUtils.toString(is, "UTF-8");

        final int statusCode = response.getStatusLine().getStatusCode();
        Log.d(TAG, "status code = " + statusCode);
        // Log.d(TAG, "output = " + output);

        if (statusCode == 401) {
            clearAuthInformation();

            if (mListener != null) {
                mListener.OnNeedsCredentials();
            }
        }

        // TODO Debug, REMOVEME
        FileUtils.writeStringToFile(
            new File(Environment.getExternalStorageDirectory().getPath() + "/" + service.replace('/', '_')), output,
            "UTF-8");

        return output;
    }

    private class RetrieveRequestTokenTask extends AsyncTask<Void, Void, String> {

        private OAuthLoginCallback mCallback;

        public RetrieveRequestTokenTask(OAuthLoginCallback callback) {
            mCallback = callback;
        }

        @Override
        protected String doInBackground(Void... params) {
            String authorizeUrl = null;
            try {
                authorizeUrl = retrieveRequestToken();
            } catch (Exception e) {
                Log.e(TAG, "Exception while retrieving request token.", e);
                if (mCallback != null) {
                    mCallback.onError(e);
                }
            }
            return authorizeUrl;
        }

        @Override
        protected void onPostExecute(String authorizeUrl) {
            if (authorizeUrl != null) {
                OAuthDialogFragment f = OAuthDialogFragment.newInstance(authorizeUrl, mOAuthCallbackUrl,
                                                                        new OAuthDialogFragment.AuthorizeListener() {

                                                                            @Override
                                                                            public void onAuthorized(String verifier) {
                                                                                new RetrieveAccessTokenTask(mCallback).execute(verifier);
                                                                            }
                                                                        });

                if (mOAuthLoginDialogType == OAuthLoginDialogType.FULLSCREEN) {
                    FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                    ft.add(android.R.id.content, f);
                    ft.commit();
                } else {
                    f.show(mActivity.getFragmentManager(), OAuthDialogFragment.TAG);
                }

                saveRequestInformation(mConsumer.getToken(), mConsumer.getTokenSecret());
            }
        }
    }

    public String retrieveRequestToken()
        throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        String s = mProvider.retrieveRequestToken(mConsumer, mOAuthCallbackUrl);
        saveRequestInformation(mConsumer.getToken(), mConsumer.getTokenSecret());
        saveAuthInformation(mConsumer.getToken(), mConsumer.getTokenSecret());
        return s;
    }

    private class RetrieveAccessTokenTask extends AsyncTask<String, Void, Boolean> {

        private OAuthLoginCallback mCallback;

        public RetrieveAccessTokenTask(OAuthLoginCallback callback) {
            mCallback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            try {
                retrieveAccessToken(params[0]);
                success = true;
            } catch (Exception e) {
                Log.e(TAG, "Exception while retrieving access token.", e);
                if (mCallback != null) {
                    mCallback.onError(e);
                }
                success = false;
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mIsLoggedIn = success;

            if (success) {
                mToken = mConsumer.getToken();
                mTokenSecret = mConsumer.getTokenSecret();
                mConsumer.setTokenWithSecret(mToken, mTokenSecret);

                saveAuthInformation(mToken, mTokenSecret);
                clearRequestInformation();

                if (mCallback != null) {
                    mCallback.onSuccess();
                }
            }
        }
    }

    public void retrieveAccessToken(String param)  {
        try {
            mProvider.retrieveAccessToken(mConsumer, param);
            mToken = mConsumer.getToken();
            mTokenSecret = mConsumer.getTokenSecret();
            mConsumer.setTokenWithSecret(mToken, mTokenSecret);

            saveAuthInformation(mToken, mTokenSecret);
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
        return mActivity.getSharedPreferences(SHARED_PREF_FILENAME, Activity.MODE_PRIVATE);
    }

    public List<Review> retrieveBooksOnShelf(String shelfName){
        List<Review> ret = null;

        try {
            Map<String, String> params = new HashMap<String, String>();
            String id = getAuthUserInfo().getId();
            params.put("id", id);
            params.put("v", "2");
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

    /**
     * Get a paginated list of an authors books.
     *
     * @return
     */
    public AuthorResponse getAuthorBooks(int authorId) {
        return getAuthorBooks(authorId, 0);
    }

    /**
     * Get a paginated list of an authors books.
     *
     * @return
     */
    public AuthorResponse getAuthorBooks(int authorId, int page) {
        AuthorResponse ret = null;

        try {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("page", Integer.toString(page));

            String output = request("author/list/" + authorId + ".xml", params);

            Serializer serializer = new Persister();

            ret = serializer.read(AuthorResponse.class, output);
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        return ret;
    }

    public Author getAuthorInfo(int authorId) {
        Author ret = null;

        try {
            String output = request("author/show/" + authorId + ".xml");

            Serializer serializer = new Persister();

            AuthorResponse response = serializer.read(AuthorResponse.class, output);
            if (response != null) {
                ret = response.getAuthor();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        return ret;
    }

    /**
     * Returns the Goodreads ID of a given book identified by it's ISBN.
     *
     * @param isbn
     *          The book identified by an ISBN
     * @return The Goodreads ID of the book
     */
    public String getIsbnToId(String isbn) {
        String ret = null;

        try {
            ret = request("book/isbn_to_id/" + isbn);
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        return ret;
    }
}
