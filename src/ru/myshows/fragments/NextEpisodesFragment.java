package ru.myshows.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.api.MyShowsApi;
import ru.myshows.api.MyShowsClient;
import ru.myshows.domain.Episode;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.GetNextEpisodesTask;
import ru.myshows.tasks.TaskListener;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.EpisodeComparator;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 06.10.11
 * Time: 1:10
 * To change this template use File | Settings | File Templates.
 */
public class NextEpisodesFragment extends Fragment implements TaskListener<List<Episode>>, Taskable {

    private SectionedAdapter adapter;
    private RelativeLayout rootView;
    private ListView list;
    private ProgressBar progress;
    private static DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    public NextEpisodesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (RelativeLayout) inflater.inflate(R.layout.next_episodes, container, false);
        progress = (ProgressBar) rootView.findViewById(R.id.progress_next_episodes);
        list = (ListView) rootView.findViewById(R.id.next_episodes_list);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        executeTask();
    }

    @Override
    public void executeTask() {
        GetNextEpisodesTask episodesTask = new GetNextEpisodesTask(getActivity());
        episodesTask.setTaskListener(this);
        episodesTask.execute();
    }

    @Override
    public void executeUpdateTask() {
        GetNextEpisodesTask episodesTask = new GetNextEpisodesTask(getActivity(), true);
        episodesTask.setTaskListener(this);
        list.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        episodesTask.execute();
    }

    @Override
    public void onTaskComplete(List<Episode> result) {
        if (isAdded())
            list.setAdapter(populateAdapter(result));
        progress.setVisibility(View.GONE);
        progress.setIndeterminate(false);
        list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskFailed(Exception e) {
        if (e != null) {
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


    public static class EpisodesAdapter extends ArrayAdapter<Episode> {
        private Context context;
        private List<Episode> episodes;


        public EpisodesAdapter(Context context, int textViewResourceId, List<Episode> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.episodes = objects;
        }

        protected class ViewHolder {
            protected TextView title;
            protected CheckBox checkBox;
            protected TextView shortTitle;
            private TextView airDate;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            final Episode episode = episodes.get(position);

            if (episode != null) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.episode, null);
                    holder = new ViewHolder();
                    holder.title = (TextView) convertView.findViewById(R.id.episode_title);
                    holder.checkBox = (CheckBox) convertView.findViewById(R.id.episode_check_box);
                    holder.shortTitle = (TextView) convertView.findViewById(R.id.episode_short_title);
                    holder.airDate = (TextView) convertView.findViewById(R.id.episode_air_date);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                try {
                    UserShow us = MyShows.getUserShow(episode.getShowId());
                    // it happens when show is not started yet but already added as watching
                    if (us == null)
                        us = new UserShow(MyShowsClient.getInstance().getShowInfo(episode.getShowId()), MyShowsApi.STATUS.watching);
                    holder.title.setText(us.getTitle());
                    holder.shortTitle.setText(episode.getShortName() != null ? episode.getShortName() : composeShortTitle(episode) + " " + episode.getTitle());
                    holder.airDate.setText(episode.getAirDate() != null ? df.format(episode.getAirDate()) : "unknown");
                } catch (NullPointerException e) {}
                holder.checkBox.setVisibility(View.GONE);
            }
            return convertView;

        }

        private String composeShortTitle(Episode e) {
            int season = e.getSeasonNumber();
            int episode = e.getEpisodeNumber();
            return ("s" + String.format("%1$02d", season) + "e" + String.format("%1$02d", episode));
        }

    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        boolean isCheked = true;

        @Override
        public void onClick(View view) {
            TextView header = (TextView) view;
            SectionedAdapter.Section s = adapter.getSection(header.getText().toString());
            for (Episode e : ((EpisodesAdapter) s.adapter).episodes) {
                e.setChecked(isCheked);
            }
            isCheked = !isCheked;
            adapter.notifyDataSetChanged();

        }
    };

    private SectionedAdapter populateAdapter(List<Episode> result) {

        if (result == null) {
            adapter = new SectionedAdapter(getActivity(), R.layout.header, null);
            return adapter;
        }

        Map<String, List<Episode>> episodesByMonth = new TreeMap<String, List<Episode>>(new Comparator<String>() {
            @Override
            public int compare(String s, String s1) {
                try {
                    int year = Integer.parseInt(s.split(":")[1]);
                    int year1 = Integer.parseInt(s1.split(":")[1]);
                    if (year != year1) {
                        if (year > year1)
                            return 1;
                        else
                            return -1;

                    }
                    int month = Integer.parseInt(s.split(":")[0]);
                    int month1 = Integer.parseInt(s1.split(":")[0]);
                    if (month > month1)
                        return 1;
                    else if (month < month1)
                        return -1;
                    else
                        return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return s.compareTo(s1);
                }
            }
        });

        for (Episode e : result) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(e.getAirDate());
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            List<Episode> temp = episodesByMonth.get(month + ":" + year);
            if (temp == null) {
                temp = new ArrayList<Episode>();
                episodesByMonth.put(month + ":" + year, temp);

            }
            temp.add(e);
        }


        List<SectionedAdapter.Section> sectionList = new ArrayList<SectionedAdapter.Section>();
        EpisodeComparator comparator = new EpisodeComparator("date");
        for (Map.Entry<String, List<Episode>> entry : episodesByMonth.entrySet()) {
            String dateString = entry.getKey();
            List<Episode> episodes = entry.getValue();
            Collections.sort(episodes, comparator);
            String[] array = dateString.split(":");
            int month = Integer.valueOf(array[0]);
            String m = getResources() != null ? getResources().getStringArray(R.array.months)[month] : getMonth(month);
            sectionList.add(new SectionedAdapter.Section(m + " " + array[1], new EpisodesAdapter(getActivity(), R.layout.episode, episodes)));
        }
        adapter = new SectionedAdapter(getActivity(), R.layout.header, sectionList);
        return adapter;

    }


    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}