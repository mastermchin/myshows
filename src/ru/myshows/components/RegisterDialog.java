package ru.myshows.components;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ru.myshows.activity.MainActivity;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsApi;
import ru.myshows.api.MyShowsClient;
import ru.myshows.util.Settings;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 10.06.2011
 * Time: 16:57:26
 * To change this template use File | Settings | File Templates.
 */
public class RegisterDialog extends Dialog {

    private Button cancelButton;
    private Button registesButton;
    private EditText loginField;
    private EditText passwordField;
    private EditText emailField;
    private RadioGroup genderField;
    private Context context;
    private MyShowsClient client = MyShowsClient.getInstance();


    public RegisterDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        cancelButton = (Button) findViewById(R.id.cancel_button);
        registesButton = (Button) findViewById(R.id.register_button);
        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        emailField = (EditText) findViewById(R.id.email_field);
        genderField = (RadioGroup) findViewById(R.id.gender);
        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        registesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = loginField.getText().toString();
                String password = passwordField.getText().toString();
                String email = emailField.getText().toString();
                MyShowsApi.GENDER gender = getGender(genderField.getCheckedRadioButtonId());
                boolean registerResult = client.register(login, password, email, gender);
                System.out.println("registerResult = " + registerResult);
                if (registerResult) {
                    boolean result = client.login(login, password);
                    if (result) {
                        Settings.setString(Settings.KEY_LOGIN, login);
                        Settings.setString(Settings.KEY_PASSWORD, password);
                        context.startActivity(new Intent(context, MainActivity.class));
                    } else {
                        Toast.makeText(context, R.string.wrong_login_or_password, Toast.LENGTH_SHORT).show();
                    }
                } else {
                     Toast.makeText(context, R.string.register_failed, Toast.LENGTH_SHORT).show();   
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterDialog.this.dismiss();
            }
        });

    }

    private MyShowsApi.GENDER getGender(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.gender_male:
                return MyShowsApi.GENDER.m;
            case R.id.gender_female:
                return MyShowsApi.GENDER.f;
            default:
                return MyShowsApi.GENDER.a;
        }
    }


}
