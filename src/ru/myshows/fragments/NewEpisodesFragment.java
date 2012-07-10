package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.ActionMode;
import android.view.Menu;
import android.widget.*;
import com.actionbarsherlock.view.*;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.domain.Episode;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.BaseTask;
import ru.myshows.tasks.GetNewEpisodesTask;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.EpisodeComparator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 06.10.11
 * Time: 1:10
 * To change this template use File | Settings | File Templates.
 */
public class NewEpisodesFragment extends Fragment implements GetNewEpisodesTask.NewEpisodesLoadingListener, Taskable {

    private SectionedAdapter adapter;
    private RelativeLayout rootView;
    private Button saveButton;
    private List<Episode> localEpisodes = null;
    private ListView list;
    private ProgressBar progress;
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    MyShows app;
    private LayoutInflater inflater;
    private boolean isTaskExecuted = false;

    public NewEpisodesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyShows) getActivity().getApplication();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = (RelativeLayout) inflater.inflate(R.layout.new_episodes, container, false);
        progress = (ProgressBar) rootView.findViewById(R.id.progress_new_episodes);
        list = (ListView) rootView.findViewById(R.id.new_episodes_list);
        return rootView;
    }


    @Override
    public void executeTask() {
        if (isTaskExecuted)
            return;
        GetNewEpisodesTask episodesTask = new GetNewEpisodesTask(getActivity());
        episodesTask.setEpisodesLoadingListener(this);
        episodesTask.execute();
    }

    @Override
    public void executeUpdateTask() {
        GetNewEpisodesTask episodesTask = new GetNewEpisodesTask(getActivity(), true);
        episodesTask.setEpisodesLoadingListener(this);
        list.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        episodesTask.execute();
    }

    @Override
    public void onNewEpisodesLoaded(List<Episode> episodes) {
        adapter = new SectionedAdapter(inflater, clickListener);
        populateAdapter(episodes);
        list.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        progress.setIndeterminate(false);
        list.setVisibility(View.VISIBLE);
        isTaskExecuted = true;
    }

    View.OnClickListener saveButtonListener = new View.OnClickListener() {
        ProgressDialog dialog = null;

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                Toast.makeText(getActivity(), msg.what, Toast.LENGTH_SHORT).show();
                removeSaveButton();
                setUpdatedAdapter();
                //new GetNewEpisodesTask(NewEpisodesFragment.this).execute();

            }
        };

        Runnable checkEpisodesTask = new Runnable() {
            public void run() {
                Map<Integer, String> paramsMap = new HashMap<Integer, String>();
                for (SectionedAdapter.Section s : adapter.getSections()) {
                    List<Episode> episodes = ((EpisodesAdapter) s.adapter).episodes;
                    for (Episode e : episodes) {
                        if (e.isChecked()) {
                            String value = paramsMap.get(e.getShowId());
                            if (value == null)
                                paramsMap.put(e.getShowId(), e.getEpisodeId().toString());
                            else
                                paramsMap.put(e.getShowId(), value += "," + e.getEpisodeId().toString());
                            MyShows.newEpisodes.remove(e);

                        }
                    }
                }

                for (Map.Entry<Integer, String> entry : paramsMap.entrySet()) {
                    final Integer showId = entry.getKey();
                    final String episodesIds = entry.getValue();
                    new Thread() {
                        public void run() {
                            UserShow userShow = app.getUserShow(showId);
                            userShow.setWatchedEpisodes(userShow.getWatchedEpisodes() + episodesIds.split(",").length);
                            //app.setUserShowsChanged(true);
                            MyShows.client.syncAllShowEpisodes(showId, episodesIds, null);
                        }
                    }.start();
                }

                int message = R.string.changes_saved;
                handler.sendEmptyMessage(message);
            }

        };

        @Override
        public void onClick(View v) {
            saveButton.setEnabled(false);
            dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.loading));
            handler.postDelayed(checkEpisodesTask, 1000);
        }

    };


    private void setUpdatedAdapter() {
        adapter = new SectionedAdapter(inflater, clickListener);
        populateAdapter(localEpisodes);
        list.setAdapter(adapter);
    }

    private Button getSaveButton() {
        if (saveButton == null) {
            saveButton = new Button(getActivity());
            saveButton.setText(R.string.save);
            saveButton.setId(1);
            saveButton.setTextColor(Color.WHITE);
            saveButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
            saveButton.setOnClickListener(saveButtonListener);

        }
        return saveButton;
    }

    private void addSaveButton() {

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonParams.setMargins(5, 5, 5, 5);
        rootView.addView(getSaveButton(), buttonParams);
        // change list layout parameters
        RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        listParams.addRule(RelativeLayout.ABOVE, getSaveButton().getId());
        listParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        list.setLayoutParams(listParams);

    }

    private void removeSaveButton() {
        rootView.removeView(saveButton);
        saveButton = null;
    }

    private class EpisodesAdapter extends ArrayAdapter<Episode> {

        private List<Episode> episodes;
        private String showTitle;
        private Episode last;


        private EpisodesAdapter(Context context, int textViewResourceId, List<Episode> objects, String showTitle) {
            super(context, textViewResourceId, objects);
            this.episodes = objects;
            this.showTitle = showTitle;
            this.last = objects.get(objects.size() - 1);
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

                holder.title.setText(episode.getTitle());
                holder.shortTitle.setText(episode.getShortName() != null ? episode.getShortName() : composeShortTitle(episode));
                holder.airDate.setText(episode.getAirDate() != null ? df.format(episode.getAirDate()) : "unknown");


                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (saveButton == null)
                            addSaveButton();
                        episode.setChecked(!episode.isChecked());
                        notifyDataSetChanged();
                    }
                });

                holder.checkBox.setChecked(episode.isChecked());
                convertView.setOnCreateContextMenuListener(null);
            }
            return convertView;

        }
    }

//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        final Episode episode = (Episode) adapter.getItem(info.position);
//
//        if (episode != null) {
//            Handler handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    MyShows.getClient().changeEpisodeRatio(msg.arg1, episode.getEpisodeId());
//                }
//            };
//            RatingDialog rate = new RatingDialog(getActivity(), handler);
//            rate.setTitle(R.string.episode_rating);
//            rate.show();
//
//        }
//    }


    private String composeShortTitle(Episode e) {
        int season = e.getSeasonNumber();
        int episode = e.getEpisodeNumber();
        return ("s" + String.format("%1$02d", season) + "e" + String.format("%1$02d", episode));
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
            if (saveButton == null) addSaveButton();
        }
    };

    private void populateAdapter(List<Episode> result) {
        if (result == null) return;
        Map<Integer, List<Episode>> episodesByShows = new HashMap<Integer, List<Episode>>();

        for (Episode e : result) {
            List<Episode> temp = episodesByShows.get(e.getShowId());
            if (temp == null) {
                temp = new ArrayList<Episode>();
                episodesByShows.put(e.getShowId(), temp);

            }
            temp.add(e);
        }

        for (Map.Entry<Integer, List<Episode>> entry : episodesByShows.entrySet()) {
            Integer showId = entry.getKey();
            String title = app.getUserShow(showId) != null ? app.getUserShow(showId).getTitle() : "test";
            List<Episode> episodes = entry.getValue();
            Collections.sort(episodes, new EpisodeComparator("shortName"));
            adapter.addSection(title + "*", new EpisodesAdapter(getActivity(), R.layout.episode, episodes, title));
        }


    }


    private final class CheckNewEpisodesActionMode implements com.actionbarsherlock.view.ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(com.actionbarsherlock.view.ActionMode mode, com.actionbarsherlock.view.Menu menu) {
            //Used to put dark icons on light action bar


            menu.add("Save")
                    .setIcon(R.drawable.ic_save)
                    .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(com.actionbarsherlock.view.ActionMode mode, com.actionbarsherlock.view.Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(com.actionbarsherlock.view.ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
            //Toast.makeText(ShowActivity.this, "Got click: " + item, Toast.LENGTH_SHORT).show();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(com.actionbarsherlock.view.ActionMode mode) {
        }
    }

}