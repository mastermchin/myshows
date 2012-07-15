package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.domain.Episode;
import ru.myshows.domain.Searchable;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.GetNewEpisodesTask;
import ru.myshows.tasks.GetNextEpisodesTask;
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
public class NextEpisodesFragment extends Fragment implements GetNextEpisodesTask.NextEpisodesLoadingListener, Taskable, Searchable {

    private SectionedAdapter adapter;
    private RelativeLayout rootView;
    private ListView list;
    private ProgressBar progress;
    private static DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private boolean isTaskExecuted = false;

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
    public void executeTask() {
        if (isTaskExecuted)
            return;
        GetNextEpisodesTask episodesTask = new GetNextEpisodesTask(getActivity());
        episodesTask.setNextEpisodesLoadingListener(this);
        episodesTask.execute();
    }

    @Override
    public void executeUpdateTask() {
        GetNextEpisodesTask episodesTask = new GetNextEpisodesTask(getActivity(), true);
        episodesTask.setNextEpisodesLoadingListener(this);
        list.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        episodesTask.execute();
    }

    @Override
    public void onNextEpisodesLoaded(List<Episode> episodes) {
        // populateAdapter(episodes);
        list.setAdapter(populateAdapter(episodes));
        progress.setVisibility(View.GONE);
        progress.setIndeterminate(false);
        list.setVisibility(View.VISIBLE);
        isTaskExecuted = true;
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

                holder.title.setText(MyShows.getUserShow(episode.getShowId()).getTitle());
                holder.shortTitle.setText(episode.getShortName() != null ? episode.getShortName() : composeShortTitle(episode) + " " + episode.getTitle());
                holder.airDate.setText(episode.getAirDate() != null ? df.format(episode.getAirDate()) : "unknown");

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

        Map<Integer, List<Episode>> episodesByMonth = new TreeMap<Integer, List<Episode>>();

        for (Episode e : result) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(e.getAirDate());
            int month = calendar.get(Calendar.MONTH);
            List<Episode> temp = episodesByMonth.get(month);
            if (temp == null) {
                temp = new ArrayList<Episode>();
                episodesByMonth.put(month, temp);

            }
            temp.add(e);
        }
        List<SectionedAdapter.Section> sectionList = new ArrayList<SectionedAdapter.Section>();

        for (Map.Entry<Integer, List<Episode>> entry : episodesByMonth.entrySet()) {
            Integer month = entry.getKey();
            List<Episode> episodes = entry.getValue();
            Collections.sort(episodes, new EpisodeComparator("date"));
            String m = new DateFormatSymbols().getMonths()[month];
            sectionList.add(new SectionedAdapter.Section(m, new EpisodesAdapter(getActivity(), R.layout.episode, episodes)));
        }
        adapter = new SectionedAdapter(getActivity(), R.layout.header, sectionList);
        return adapter;

    }


    @Override
    public ArrayAdapter getAdapter() {
        return adapter;
    }
}