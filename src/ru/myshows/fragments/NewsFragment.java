package ru.myshows.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.ProfileActivity;
import ru.myshows.activity.R;
import ru.myshows.activity.ShowActivity;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.domain.Searchable;
import ru.myshows.domain.UserNews;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.GetNewsTask;
import ru.myshows.tasks.TaskListener;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.NewsComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:19:10
 * To change this template use File | Settings | File Templates.
 */
public class NewsFragment extends Fragment implements TaskListener<Map<String, List<UserNews>>>, Taskable, Searchable {


    private SectionedAdapter adapter;
    private ListView list;
    private ProgressBar progress;
    private LayoutInflater inflater;
    private boolean isTaskExecuted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.news, container, false);
        list = (ListView) view.findViewById(R.id.news_list);
        progress = (ProgressBar) view.findViewById(R.id.progress_news);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        executeTask();
    }

    @Override
    public void onTaskComplete(Map<String, List<UserNews>> result) {
        if (isAdded())
            list.setAdapter( populateAdapter(result));
        progress.setVisibility(View.GONE);
        progress.setIndeterminate(false);
        list.setVisibility(View.VISIBLE);
        isTaskExecuted = true;
    }

    @Override
    public void onTaskFailed(Exception e) {
        if (e != null){
            progress.setVisibility(View.GONE);
        }
        final AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.something_wrong)
                .setMessage(R.string.try_again)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executeUpdateTask();
                    }
                })
                .setNegativeButton(R.string.no, null);
        alert = builder.create();
        alert.show();
    }


    @Override
    public void executeTask() {
        if (isTaskExecuted)
            return;
        GetNewsTask newsTask = new GetNewsTask(getActivity());
        newsTask.setTaskListener(this);
        newsTask.execute();
    }

    @Override
    public void executeUpdateTask() {
        GetNewsTask newsTask = new GetNewsTask(getActivity(), true);
        newsTask.setTaskListener(this);
        list.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        newsTask.execute();
    }

    public static class NewsAdapter extends ArrayAdapter<UserNews> {

        private Context context;
        private List<UserNews> news;


        public NewsAdapter(Context context, int textViewResourceId, List<UserNews> shows) {
            super(context, textViewResourceId, shows);
            this.context = context;
            this.news = shows;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        intent.setClass(context, ProfileActivity.class);
                        context.startActivity(intent);
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
                        intent.putExtra("title", n.getShow());
                        UserShow userShow = MyShows.getUserShow(showId);
                        if (userShow != null) {
                            intent.putExtra("watchStatus", userShow.getWatchStatus());
                            intent.putExtra("yoursRating", userShow.getYoursRating());
                        }
                        intent.setClass(context, ShowActivity.class);
                        context.startActivity(intent);
                    }


                }
            });

            return convertView;
        }

        private String composeAction(UserNews news) {
            String action = "";
            if (news.getAction() != null && news.getAction().equals("watch"))
                action = context.getResources().getString(R.string.action_watched) + " ";
            else
                action = news.getAction() + " ";

            if (news.getEpisodes() == 1)
                action += news.getEpisode();
            else
                action += news.getEpisodes();
            return action;
        }

        protected class ViewHolder {
            protected TextView login;
            protected TextView action;
            protected TextView show;
        }


    }


    private SectionedAdapter populateAdapter(Map<String, List<UserNews>> news) {

        if (news == null || news.isEmpty()) {
            adapter = new SectionedAdapter(getActivity(), R.layout.header, null);
            return adapter;
        }

        List<SectionedAdapter.Section> sectionList = new ArrayList<SectionedAdapter.Section>();

        TreeMap<String, List<UserNews>> m = new TreeMap<String, List<UserNews>>(new NewsComparator());
        m.putAll(news);
        for (Map.Entry<String, List<UserNews>> entry : m.entrySet()) {
            String key = entry.getKey();
            List<UserNews> value = entry.getValue();

            sectionList.add(new SectionedAdapter.Section(key, new NewsAdapter(getActivity(), R.layout.news_item, value)));

        }
        adapter = new SectionedAdapter(getActivity(), R.layout.header, sectionList);
        return adapter;
    }


    @Override
    public ArrayAdapter getAdapter() {
        return adapter;
    }

}
