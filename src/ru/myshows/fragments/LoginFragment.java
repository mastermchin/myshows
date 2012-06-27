package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.myshows.activity.MainActivity;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.client.MyShowsClient;
import ru.myshows.prefs.Settings;

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
    private MyShowsClient client = MyShowsClient.getInstance();
    MyShows app;
    private LayoutInflater inflater;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
       // return inflater.inflate(R.layout.login, container, false);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.login, container, false);
        loginButton = (Button) layout.findViewById(R.id.login_button);
        registesButton = (Button) layout.findViewById(R.id.register_button);
        loginField = (EditText) layout.findViewById(R.id.login_field);
        passwordField = (EditText) layout.findViewById(R.id.password_field);


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

        return layout;

    }


    private void loginResult(Boolean result, String login, String password) {
        if (result) {
            Settings.setString(Settings.KEY_LOGIN, login);
            Settings.setString(Settings.KEY_PASSWORD, password);
            Settings.setBoolean(Settings.KEY_LOGGED_IN, true);
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
        } else {
            showError();
        }
    }

    private void showError() {
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
