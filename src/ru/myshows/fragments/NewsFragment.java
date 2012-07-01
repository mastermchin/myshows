package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.*;
import ru.myshows.activity.MainActivity;
import ru.myshows.activity.R;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.api.MyShowsClient;
import ru.myshows.domain.UserNews;
import ru.myshows.tasks.GetNewsTask;
import ru.myshows.util.NewsComparator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:19:10
 * To change this template use File | Settings | File Templates.
 */
public class NewsFragment extends Fragment implements GetNewsTask.NewsLoadingListener{


    private SectionedAdapter adapter;
    private ListView  list;
    private ProgressBar progress;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view =  inflater.inflate(R.layout.news, container, false);
        list =     (ListView) view.findViewById(R.id.news_list);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        return view;
    }

    @Override
    public void onNewsLoaded(Map<String, List<UserNews>> news) {
        adapter = new SectionedAdapter(inflater);
        populateAdapter(news);
        list.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        progress.setIndeterminate(false);
        list.setVisibility(View.VISIBLE);
    }

    private class NewsAdapter extends ArrayAdapter<UserNews> {

        private List<UserNews> news;
        private String section;
        private UserNews last;


        private NewsAdapter(Context context, int textViewResourceId, List<UserNews> shows, String section) {
            super(context, textViewResourceId, shows);
            this.news = shows;
            this.section = section;
            this.last = shows.get(shows.size() - 1);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.news_item, null);
                holder = new ViewHolder();
                holder.login = (TextView) convertView.findViewById(R.id.news_login);
                holder.action = (TextView) convertView.findViewById(R.id.news_action);
                holder.show = (TextView) convertView.findViewById(R.id.news_show);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final UserNews n = news.get(position);
            if (n != null) {
                holder.login.setText(n.getLogin() != null ? n.getLogin() : "login");
                holder.action.setText(n.getAction() != null ? composeAction(n) : "action");
                holder.show.setText(n.getShow() != null ? n.getShow() : "show");
            }

            holder.login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String login = n.getLogin();
                    if (login != null && login.trim().length() > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("login", login);
                        intent.setClass(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });


            holder.show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer showId = n.getShowId();
                    if (showId != null) {
                        Intent intent = new Intent();
                        intent.putExtra("showId", showId);
                        intent.setClass(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }


                }
            });

            return convertView;
        }

        private String composeAction(UserNews news) {
            String action = "";
            if (news.getAction() != null && news.getAction().equals("watch"))
                action = getResources().getString(R.string.action_watched) + " ";
            else
                action = news.getAction() + " ";

            if (news.getEpisodes() == 1)
                action +=  getResources().getString(R.string.episode) + " " +  news.getEpisode();
            else
                action += news.getEpisodes() + " " + getResources().getString(R.string.episodes);
            return action;
        }

        protected class ViewHolder {
            protected TextView login;
            protected TextView action;
            protected TextView show;
        }


    }


    private void populateAdapter( Map<String, List<UserNews>> news) {
        TreeMap<String, List<UserNews>> m = new TreeMap<String, List<UserNews>>(new NewsComparator());
        m.putAll(news);
        for (Map.Entry<String, List<UserNews>> entry : m.entrySet()) {
            String key = entry.getKey();
            List<UserNews> value = entry.getValue();

            adapter.addSection(key, new NewsAdapter(getActivity(), R.layout.news_item, value, key));

        }

    }
}
