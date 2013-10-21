package ru.myshows.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import ru.myshows.activity.MainActivity;
import ru.myshows.activity.OAuthActivity;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsClient;
import ru.myshows.util.Settings;


/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 07.06.2011
 * Time: 2:09:00
 * To change this template use File | Settings | File Templates.
 */
public class LoginFragment extends Fragment {

    private Button loginButton;
    private Button registesButton;
    private EditText loginField;
    private EditText passwordField;
    private Button loginFacebook;
    private Button loginTwitter;
    private Button loginVk;
    private MyShowsClient client = MyShowsClient.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView layout = (ScrollView) inflater.inflate(R.layout.login, container, false);
        loginButton = (Button) layout.findViewById(R.id.login_button);
        registesButton = (Button) layout.findViewById(R.id.register_button);
        loginField = (EditText) layout.findViewById(R.id.login_field);
        passwordField = (EditText) layout.findViewById(R.id.password_field);
        loginFacebook = (Button) layout.findViewById(R.id.login_facebook);
        loginTwitter = (Button) layout.findViewById(R.id.login_twitter);
        loginVk = (Button) layout.findViewById(R.id.login_vk);


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginButton.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginField.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(passwordField.getWindowToken(), 0);
                String login = loginField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                new LoginTask(getActivity()).execute(login, password);
                loginButton.setEnabled(true);
            }
        });


        registesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registesButton.setEnabled(false);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://myshows.ru/registration"));
                startActivity(i);
                registesButton.setEnabled(true);

            }
        });


        loginTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                intent.putExtra("type", OAuthActivity.OAUTH_TWITTER);
                startActivityForResult(intent, OAuthActivity.OAUTH_TWITTER);
            }
        });

        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                intent.putExtra("type", OAuthActivity.OAUTH_FACEBOOK);
                startActivityForResult(intent, OAuthActivity.OAUTH_FACEBOOK);
            }
        });


        loginVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                intent.putExtra("type", OAuthActivity.OAUTH_VK);
                startActivityForResult(intent, OAuthActivity.OAUTH_VK);
            }
        });


        OAuthActivity.OAuthListener oAuthListener = new OAuthActivity.OAuthListener() {

            @Override
            public void onLogin(int oAuthType, String token, String userId, String secret) {
                Log.d("MyShows", "Execute login social task...");
                new LoginSocialTask(getActivity()).execute(oAuthType, token, userId, secret);
            }

            @Override
            public void onError() {

            }
        };

        OAuthActivity.oAuthListener = oAuthListener;

        return layout;

    }


    private class LoginTask extends AsyncTask<Object, Void, Boolean> {
        private ProgressDialog dialog;
        private String login;
        private String pass;

        private LoginTask(Context context) {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            dialog.show();

        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            this.login = (String) objects[0];
            this.pass = (String) objects[1];
            return client.login(login, pass);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (result) {
                Settings.setString(Settings.KEY_LOGIN, login);
                Settings.setString(Settings.KEY_PASSWORD, pass);
                Settings.setBoolean(Settings.KEY_LOGGED_IN, true);
                if (isAdded())
                    getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                if (isAdded())
                    Toast.makeText(getActivity(), R.string.wrong_login_or_password, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class LoginSocialTask extends AsyncTask<Object, Void, Boolean> {
        private ProgressDialog dialog;
        private Integer oAuthType;
        private String token;
        private String userId;
        private String secret;

        private LoginSocialTask(Context context) {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            dialog.show();

        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            oAuthType = (Integer) objects[0];
            token = (String) objects[1];
            userId = (String) objects[2];

            if (oAuthType == OAuthActivity.OAUTH_TWITTER)
                secret = (String) objects[3];

            return client.loginSocial(oAuthType, token, userId, secret);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (this.dialog.isShowing())
                this.dialog.dismiss();
            if (result) {


                String isLoggedInKey = null;
                String tokenKey = null;
                String userIdKey = null;

                switch (oAuthType) {
                    case OAuthActivity.OAUTH_FACEBOOK:
                        isLoggedInKey = Settings.FACEBOOK_IS_LOGGED_IN;
                        token = Settings.FACEBOOK_TOKEN;
                        userIdKey = Settings.FACEBOOK_USER_ID;
                        break;
                    case OAuthActivity.OAUTH_VK:
                        isLoggedInKey = Settings.VK_IS_LOGGED_IN;
                        tokenKey = Settings.VK_TOKEN;
                        userIdKey = Settings.VK_USER_ID;
                        break;
                    case OAuthActivity.OAUTH_TWITTER:
                        isLoggedInKey = Settings.TWITTER_IS_LOGGED_IN;
                        tokenKey = Settings.TWITTER_TOKEN;
                        userIdKey = Settings.TWITTER_USER_ID;
                        break;
                }


                Settings.setBoolean(isLoggedInKey, true);
                Settings.setString(tokenKey, token);
                Settings.setString(userIdKey, userId);
                if (oAuthType == OAuthActivity.OAUTH_TWITTER)
                    Settings.setString(Settings.TWITTER_SECRET, secret);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } else {

            }
        }

    }


}
