package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.model.GraphUser;
import ru.myshows.activity.MainActivity;
import ru.myshows.activity.OAuthActivity;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsClient;
import ru.myshows.util.Settings;
import ru.myshows.util.TwitterUtil;
import twitter4j.auth.RequestToken;

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
                startActivity(intent);
            }
        });

        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Session.openActiveSession(getActivity(), LoginFragment.this, true, new Session.StatusCallback() {
//                    @Override
//                    public void call(final Session session, SessionState state, Exception exception) {
//                        Log.d("MyShows", "Facebook session is opened =  " +  session.isOpened() + " session is closed = " + session.isClosed());
//                        Log.d("MyShows", "Facebook access token = " +  session.getAccessToken());
//                        Log.d("MyShows", "Facebook access token exp date = " + session.getExpirationDate());
//
//                        if (session.isOpened()) {
//                            // Request user data and show the results
//                            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//                                @Override
//                                public void onCompleted(GraphUser user, Response response) {
//                                    if (user != null) {
//                                        Log.d("MyShows", "Facebook access user id= " + user.getId());
//
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });

                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                intent.putExtra("type", OAuthActivity.OAUTH_FACEBOOK);
                startActivity(intent);
            }
        });


        loginVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                intent.putExtra("type", OAuthActivity.OAUTH_VK);
                startActivity(intent);
            }
        });

        return layout;

    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
//    }




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

    private class LoginTask extends AsyncTask {
        private Context context;
        private ProgressDialog dialog;
        private String login;
        private String pass;

        private LoginTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();

        }

        @Override
        protected Object doInBackground(Object... objects) {
            this.login = (String) objects[0];
            this.pass = (String) objects[1];
            Boolean result = client.login(login, pass);
            return result;

        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing())
                this.dialog.dismiss();
            loginResult((Boolean) result, login, pass);
        }

    }

}
