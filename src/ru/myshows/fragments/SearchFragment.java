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

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 07.06.2011
 * Time: 2:05:10
 * To change this template use File | Settings | File Templates.
 */
public class SearchFragment extends Fragment {

    private LinearLayout favouritesLayout;
    private LinearLayout catalog_layout;
    private EditText searchField;
    private Button searchButton;
    private Button favourites;
    private Button catalog;
    private LayoutInflater inflater;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
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


//        favouritesLayout = (LinearLayout) view.findViewById(R.id.favourites_layout);
//        favouritesLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                favouritesLayout.setEnabled(false);
//                startShowsActivity("top");
//                favouritesLayout.setEnabled(true);
//            }
//        });
//
//
//        catalog_layout = (LinearLayout) view.findViewById(R.id.catalog_layout);
//        catalog_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                catalog_layout.setEnabled(false);
//                startShowsActivity("all");
//                catalog_layout.setEnabled(true);
//
//            }
//        });

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

}
