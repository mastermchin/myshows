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

    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_SECRET = "secret";



    private WebView webView;
    private static Twitter twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth);

        int type = getIntent().getIntExtra("type", -1);
        Uri uri = getIntent().getData();

        // check if twitter oAuth response
        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            String arg = uri.getQueryParameter(TWITTER_OAUTH_VERIFIER);
            new TwitterGetAccessTokenTask().execute(arg);

        }

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
                        Log.i("MyShows", "url=" + url);

                        if (!isEmptyString(url) && url.startsWith(VK_REDIRECT_URL)){
                            if (!url.contains("error=")){
                                String accessToken = extractPattern(url, "access_token=(.*?)&");
                                String userId = extractPattern(url, "user_id=(\\d*)");
                                Intent intent = new Intent();
                                intent.putExtra(KEY_TOKEN,accessToken);
                                intent.putExtra(KEY_USER_ID, userId);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }else
                                Log.i("MyShows", "ERROR url=" + url);
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

                        if (!isEmptyString(url) && url.startsWith(FACEBOOK_REDIRECT_URL)){
                            if (!url.contains("error=")){
                                String accessToken = extractPattern(url, "access_token=(.*?)&");
                                new FacebookGetUserIdTask().execute(accessToken);
                            }else
                                Log.i("MyShows", "ERROR url=" + url);
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


    public static String[] parseRedirectUrl(String url) throws Exception {
        //url is something like http://api.vkontakte.ru/blank.html#access_token=66e8f7a266af0dd477fcd3916366b17436e66af77ac352aeb270be99df7deeb&expires_in=0&user_id=7657164
        String accessToken = extractPattern(url, "access_token=(.*?)&");
        String userId = extractPattern(url, "user_id=(\\d*)");
        if (isEmptyString(accessToken) || isEmptyString(userId))
            throw new Exception("Failed to parse redirect url " + url);
        return new String[]{accessToken, userId};
    }

    private static boolean isEmptyString(String s) {
        return s == null || s.length() == 0;
    }


    public static String extractPattern(String string, String pattern) {
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
                return twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class TwitterGetAccessTokenTask extends AsyncTask<String, String, AccessToken> {

        @Override
        protected AccessToken doInBackground(String... params) {

            RequestToken requestToken = null;
            try {
                requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                if (params[0] != null) {
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

                Log.d("MyShows", "access twitter token = " + accessToken.getToken());
                Log.d("MyShows", "access twitter secret = " + accessToken.getTokenSecret());
                Log.d("MyShows", "access twitter login = " + accessToken.getScreenName());

                Intent intent = new Intent();
                intent.putExtra(KEY_TOKEN, accessToken.getToken());
                intent.putExtra(KEY_SECRET, accessToken.getTokenSecret());
                intent.putExtra(KEY_USER_ID, accessToken.getScreenName());
                setResult(Activity.RESULT_OK, intent);
                finish();
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
            Intent intent = new Intent();
            Log.i("MyShows", "facebook token 2 token =" + token);
            Log.i("MyShows", "facebook user_id 2 =" + id);
            intent.putExtra(KEY_TOKEN, token);
            intent.putExtra(KEY_USER_ID, id);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


}
