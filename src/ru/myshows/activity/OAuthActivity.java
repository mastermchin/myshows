package ru.myshows.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Georgy Gobozov
 * Date: 14.10.13
 */
public class OAuthActivity extends Activity {

    public static final int OAUTH_TWITTER = 1;
    public static final int OAUTH_FACEBOOK = 2;
    public static final int OAUTH_VK = 3;

    public static final String FACEBOOK_APP_ID = "568473939831654";
    public static final String FACEBOOK_SECRET = "cb0262c6051d38e11674871822be646f";
    public static final String FACEBOOK_REDIRECT_URL = "https://www.facebook.com/connect/login_success.html";
    public static final String FACEBOOK_LOGIN_URL = "https://www.facebook.com/dialog/oauth?client_id=%1$s&redirect_uri=%2$s&response_type=token";

    public static final String VK_APP_ID = "2155950";
    public static final String VK_REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String VK_LOGIN_URl = "https://oauth.vk.com/authorize?client_id=%1$s&display=mobile&scope=&redirect_uri=%2$s&response_type=token&v=5.2";


    public static final String TWITTER_APP_ID = "LnjZiH7g5XzmmiYwOwrg";
    public static final String TWITTER_SECRET = "iVW8PHRYkkTcvqscL6yjwcoDwJxR3esNaThnxHmU";
    public static final String TWITTER_CALLBACK_URL = "oauth://ru.myshows.activity.Twitter_oAuth";
    public static final String TWITTER_OAUTH_VERIFIER = "oauth_verifier";


    private WebView webView;
    private Twitter twitter;
    private RequestToken requestToken;

    public static OAuthListener oAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth);

        int type = getIntent().getIntExtra("type", -1);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);


        switch (type) {
            case OAUTH_VK:

                String vkUrl = String.format(VK_LOGIN_URl, VK_APP_ID, URLEncoder.encode(VK_REDIRECT_URL));
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);

                        if (!isEmptyString(url) && url.startsWith(VK_REDIRECT_URL)) {
                            if (!url.contains("error=")) {
                                String accessToken = extractPattern(url, "access_token=(.*?)&");
                                String userId = extractPattern(url, "user_id=(\\d*)");

                                if (oAuthListener != null) {
                                    oAuthListener.onLogin(OAUTH_VK, accessToken, userId, null);
                                    finish();
                                }

                            } else {
                                if (oAuthListener != null)
                                    oAuthListener.onError();
                                Log.i("MyShows", "ERROR url=" + url);
                            }

                        }
                    }
                });
                webView.loadUrl(vkUrl);
                break;

            case OAUTH_FACEBOOK:

                String facebookUrl = String.format(FACEBOOK_LOGIN_URL, FACEBOOK_APP_ID, URLEncoder.encode(FACEBOOK_REDIRECT_URL));
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);

                        if (!isEmptyString(url) && url.startsWith(FACEBOOK_REDIRECT_URL)) {
                            if (!url.contains("error=")) {
                                String accessToken = extractPattern(url, "access_token=(.*?)&");
                                new FacebookGetUserIdTask().execute(accessToken);
                            } else{
                                if (oAuthListener != null)
                                    oAuthListener.onError();
                                Log.i("MyShows", "ERROR url=" + url);
                            }
                        }
                    }
                });

                webView.loadUrl(facebookUrl);
                break;


            case OAUTH_TWITTER:
                new TwitterAuthenticateTask().execute();
                break;
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();

        // check if twitter oAuth response
        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            String arg = uri.getQueryParameter(TWITTER_OAUTH_VERIFIER);
            new TwitterGetAccessTokenTask().execute(arg);

        }


    }

    private boolean isEmptyString(String s) {
        return s == null || s.length() == 0;
    }


    public String extractPattern(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (!m.find())
            return null;
        return m.toMatchResult().group(1);
    }

    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
            startActivity(intent);
        }

        @Override
        protected RequestToken doInBackground(String... params) {

            if (twitter == null) {
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                configurationBuilder.setOAuthConsumerKey(TWITTER_APP_ID);
                configurationBuilder.setOAuthConsumerSecret(TWITTER_SECRET);
                Configuration configuration = configurationBuilder.build();
                TwitterFactory twitterFactory = new TwitterFactory(configuration);
                twitter = twitterFactory.getInstance();
            }

            try {
                requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                return requestToken;
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class TwitterGetAccessTokenTask extends AsyncTask<String, String, AccessToken> {

        @Override
        protected AccessToken doInBackground(String... params) {

            try {
                if (params[0] != null && requestToken != null) {
                    return twitter.getOAuthAccessToken(requestToken, params[0]);
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            if (accessToken != null) {

                if (oAuthListener != null) {
                    oAuthListener.onLogin(OAUTH_TWITTER, accessToken.getToken(), accessToken.getUserId() + "", accessToken.getTokenSecret());
                    finish();
                }

            }  else{

                if (oAuthListener != null)
                    oAuthListener.onError();

            }
        }
    }


    class FacebookGetUserIdTask extends AsyncTask<String, String, String> {

        private String token;

        @Override
        protected String doInBackground(String... strings) {
            //https://graph.facebook.com/me?fields=id&access_token="xxxxx"
            token = strings[0];
            String url = "https://graph.facebook.com/me?fields=id&access_token=" + token;
            JSONObject object = MyShows.client.executeExternalWithJson(url);
            if (object != null)
                try {
                    return object.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }


        @Override
        protected void onPostExecute(String id) {
            if (oAuthListener != null && id != null) {

                oAuthListener.onLogin(OAUTH_FACEBOOK, token, id, null);
                finish();

            } else {

                if (oAuthListener != null)
                    oAuthListener.onError();

            }

        }
    }

    public static interface OAuthListener {

        public void onLogin(int oAuthType, String token, String userId, String secret);

        public void onError();

    }


}
