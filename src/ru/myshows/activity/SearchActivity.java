package ru.myshows.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import ru.myshows.util.MyShowsUtil;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 07.06.2011
 * Time: 2:05:10
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends Activity {

    private LinearLayout favouritesLayout;
    private LinearLayout catalog_layout;
    private EditText searchField;
    private Button searchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchField = (EditText) findViewById(R.id.search_box);
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButton.setEnabled(false);
                String query = searchField.getText().toString();
                if (query.equals("") || query.trim().length() < 1)
                    Toast.makeText(getParent(), R.string.search_query, Toast.LENGTH_SHORT).show();
                else
                    startShowsActivity(query);
                searchButton.setEnabled(true);
            }
        });


        favouritesLayout = (LinearLayout) findViewById(R.id.favourites_layout);
        favouritesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouritesLayout.setEnabled(false);
                startShowsActivity("top");
                favouritesLayout.setEnabled(true);
            }
        });


        catalog_layout = (LinearLayout) findViewById(R.id.catalog_layout);
        catalog_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catalog_layout.setEnabled(false);
                startShowsActivity("all");
                catalog_layout.setEnabled(true);

            }
        });


    }



    private void startShowsActivity(String searchString) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
        Intent intent = new Intent();
        intent.putExtra("search", searchString);
        intent.setClass(getParent(), ShowsActivity.class);
        ActivityStack activityStack = (ActivityStack) getParent();
        activityStack.push("Shows2Activity", intent);
    }

}
