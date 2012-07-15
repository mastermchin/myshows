package ru.myshows.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.myshows.activity.R;
import ru.myshows.activity.SearchActivity;
import ru.myshows.tasks.Taskable;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 07.06.2011
 * Time: 2:05:10
 * To change this template use File | Settings | File Templates.
 */
public class SearchFragment extends Fragment implements Taskable{

    private EditText searchField;
    private Button searchButton;
    private Button favourites;
    private Button catalog;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);
        searchField = (EditText) view.findViewById(R.id.search_box);
        searchButton = (Button) view.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButton.setEnabled(false);
                String query = searchField.getText().toString();
                if (query.equals("") || query.trim().length() < 1)
                    Toast.makeText(getActivity(), R.string.search_query, Toast.LENGTH_SHORT).show();
                else
                    startShowsActivity(query);
                searchButton.setEnabled(true);
            }
        });

        catalog = (Button) view.findViewById(R.id.catalog_image);
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShowsActivity("all");
            }
        });
        favourites = (Button) view.findViewById(R.id.favourites_image);
        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShowsActivity("top");
            }
        });



        return view;
    }



    private void startShowsActivity(String searchString) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
        Intent intent = new Intent();
        intent.putExtra("search", searchString);
        intent.setClass(getActivity(), SearchActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void executeTask() {
    }

    @Override
    public void executeUpdateTask() {
    }
}
