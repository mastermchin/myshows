package ru.myshows.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public static final int OAUTH_TWITTER = 1;
    public static final int OAUTH_FACEBOOK = 2;
    public static final int OAUTH_VK = 3;

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
                startActivityForResult(intent, OAUTH_TWITTER);
            }
        });

        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                intent.putExtra("type", OAuthActivity.OAUTH_FACEBOOK);
                startActivityForResult(intent, OAUTH_FACEBOOK);
            }
        });


        loginVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                intent.putExtra("type", OAuthActivity.OAUTH_VK);
                startActivityForResult(intent, OAUTH_VK);
            }
        });

        return layout;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == OAUTH_TWITTER  && resultCode == Activity.RESULT_OK && null != data)) {
            String token = data.getStringExtra("token");
            String secret = data.getStringExtra("secret");
            String userId = data.getStringExtra("user_id");

            Log.d("MyShows", "Login Fragment token = " + token);
            Log.d("MyShows", "Login Fragment secret = " + secret);
            Log.d("MyShows", "Login Fragment userId = " + userId);
        }

        if ((requestCode == OAUTH_FACEBOOK  && resultCode == Activity.RESULT_OK && null != data)) {
            String token = data.getStringExtra("token");
            String userId = data.getStringExtra("user_id");

            Log.d("MyShows", "Login Fragment token = " + token);
            Log.d("MyShows", "Login Fragment userId = " + userId);
        }

        if ((requestCode == OAUTH_VK  && resultCode == Activity.RESULT_OK && null != data)) {
            String token = data.getStringExtra("token");
            String userId = data.getStringExtra("user_id");

            Log.d("MyShows", "Login Fragment token = " + token);
            Log.d("MyShows", "Login Fragment userId = " + userId);
        }


    }




    private void loginResult(Boolean result, String login, String password) {
        if (result) {
            Settings.setString(Settings.KEY_LOGIN, login);
            Settings.setString(Settings.KEY_PASSWORD, password);
            Settings.setBoolean(Settings.KEY_LOGGED_IN, true);
            if (isAdded())
                getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
        } else {
            showError();
        }
    }

    private void showError() {
        if (isAdded())
            Toast.makeText(getActivity(), R.string.wrong_login_or_password, Toast.LENGTH_SHORT).show();
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
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();

        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            this.login = (String) objects[0];
            this.pass = (String) objects[1];
            return  client.login(login, pass);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (this.dialog.isShowing())
                this.dialog.dismiss();
            loginResult(result, login, pass);
        }

    }

}
