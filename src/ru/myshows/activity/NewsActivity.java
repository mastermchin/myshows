package ru.myshows.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import ru.myshows.api.MyShowsApi;
import ru.myshows.client.MyShowsClient;
import ru.myshows.domain.IShow;
import ru.myshows.domain.UserNews;
import ru.myshows.domain.UserShow;
import ru.myshows.util.MyShowsUtil;
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
public class NewsActivity extends ListActivity {

    MyShowsClient client = MyShowsClient.getInstance();
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    private SectionedAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        new GetNewsTask(getParent()).execute();
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
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        intent.setClass(NewsActivity.this, MainActivity.class);
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
                        intent.setClass(NewsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }


                }
            });


            if (n.equals(last)) {
                    ((ImageView) convertView.findViewById(R.id.divider)).setBackgroundDrawable(null);
                } else {
                    Drawable divider = getResources().getDrawable(R.drawable.divider_horizontal_dim_dark);
                    ((ImageView) convertView.findViewById(R.id.divider)).setBackgroundDrawable(divider);
                }

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

    private class GetNewsTask extends AsyncTask {
        private ProgressDialog dialog;

        private GetNewsTask(Context context) {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();
        }

        @Override
        protected Map<String, List<UserNews>> doInBackground(Object... objects) {
            Map<String, List<UserNews>> news = client.getNews();
            return news;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing()) this.dialog.dismiss();
            if (result != null) {
                adapter = new SectionedAdapter(getLayoutInflater());
                populateAdapter(result);
                NewsActivity.this.setListAdapter(adapter);
            }

        }

    }

    private void populateAdapter(Object result) {
        Map<String, List<UserNews>> news = (Map<String, List<UserNews>>) result;
        TreeMap<String, List<UserNews>> m = new TreeMap<String, List<UserNews>>(new NewsComparator());
        m.putAll(news);
        for (Map.Entry<String, List<UserNews>> entry : m.entrySet()) {
            String key = entry.getKey();
            List<UserNews> value = entry.getValue();

            adapter.addSection(key, new NewsAdapter(this, R.layout.news_item, value, key));

        }

    }
}
