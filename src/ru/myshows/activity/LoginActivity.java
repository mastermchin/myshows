package ru.myshows.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ru.myshows.client.MyShowsClient;
import ru.myshows.components.RegisterDialog;
import ru.myshows.prefs.Prefs;
import ru.myshows.util.MyShowsUtil;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 07.06.2011
 * Time: 2:09:00
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity {

    private Button loginButton;
    private Button registesButton;
    private EditText loginField;
    private EditText passwordField;
    private MyShowsClient client = MyShowsClient.getInstance();
    MyShows app;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        app = (MyShows) getApplication();
        loginButton = (Button) findViewById(R.id.login_button);
        registesButton = (Button) findViewById(R.id.register_button);
        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (EditText) findViewById(R.id.password_field);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginButton.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginField.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(passwordField.getWindowToken(), 0);
                String login = loginField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                new LoginTask(LoginActivity.this).execute(login, password);
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


    }

    private void loginResult(Boolean result, String login, String password) {
        if (result) {
            Prefs.setStringPrefs(LoginActivity.this, Prefs.KEY_LOGIN, login);
            Prefs.setStringPrefs(LoginActivity.this, Prefs.KEY_PASSWORD, password);
            app.setLoggedIn(result);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            showError();
            app.setLoggedIn(result);
        }
    }

    private void showError() {
        Toast.makeText(getApplicationContext(), R.string.wrong_login_or_password, Toast.LENGTH_SHORT).show();
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
            if (this.dialog.isShowing()) this.dialog.dismiss();
            if (result == null) loginResult(false, login, pass);
            loginResult((Boolean) result, login, pass);

        }

    }

}
