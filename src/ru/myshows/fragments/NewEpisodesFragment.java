package ru.myshows.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsClient;
import ru.myshows.components.RatingDialog;
import ru.myshows.domain.Episode;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.*;
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
public class NewEpisodesFragment extends Fragment implements TaskListener<List<Episode>>, Taskable {

    private MyExpandableListAdapter adapter;
    private RelativeLayout rootView;
    private ExpandableListView list;
    private ProgressBar progress;
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private LayoutInflater inflater;
    private ActionMode mMode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = (RelativeLayout) inflater.inflate(R.layout.new_episodes, container, false);
        progress = (ProgressBar) rootView.findViewById(R.id.progress_new_episodes);
        list = (ExpandableListView) rootView.findViewById(R.id.new_episodes_list);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // obtaining user shows before
        GetShowsTask task = new GetShowsTask(getActivity(), GetShowsTask.SHOWS_USER);
        task.setTaskListener(new TaskListener() {
            @Override
            public void onTaskComplete(Object result) {
                executeTask();
            }

            @Override
            public void onTaskFailed(Exception e) {

            }
        });
        task.execute();
    }


    @Override
    public void executeTask() {
        GetNewEpisodesTask episodesTask = new GetNewEpisodesTask(getActivity());
        episodesTask.setTaskListener(this);
        episodesTask.execute();
    }

    @Override
    public void executeUpdateTask() {
        GetNewEpisodesTask episodesTask = new GetNewEpisodesTask(getActivity(), true);
        episodesTask.setTaskListener(this);
        list.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        episodesTask.execute();
    }

    @Override
    public void onTaskComplete(List<Episode> result) {
        adapter = new MyExpandableListAdapter(result);
        list.setAdapter(adapter);

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


    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private LinkedList<UserShow> shows = new LinkedList<UserShow>();
        private LinkedList<List<Episode>> children = new LinkedList<List<Episode>>();


        public MyExpandableListAdapter(Collection<Episode> eps) {

            if (eps == null || eps.isEmpty())
                return;


            Map<Integer, List<Episode>> episodesByShows = new HashMap<Integer, List<Episode>>();

            for (Episode e : eps) {
                // exclude specials episodes
                if (e.getEpisodeNumber() == 0)
                    continue;

                List<Episode> temp = episodesByShows.get(e.getShowId());
                if (temp == null) {
                    temp = new ArrayList<Episode>();
                    episodesByShows.put(e.getShowId(), temp);

                }
                temp.add(e);
            }

            for (Map.Entry<Integer, List<Episode>> entry : episodesByShows.entrySet()) {
                Integer showId = entry.getKey();
                UserShow show = MyShows.getUserShow(showId);
                if (show == null)
                    continue;
                List<Episode> episodes = entry.getValue();
                Collections.sort(episodes, new EpisodeComparator("shortName"));
                shows.add(show);
                children.add(episodes);
            }

        }

        public Object getAllChildrenAsList() {
            ArrayList<Episode> episodes = new ArrayList<Episode>();
            for (Iterator<List<Episode>> i = children.iterator(); i.hasNext(); ) {
                List<Episode> list = i.next();
                episodes.addAll(list);
            }
            return episodes;
        }


        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }


        public void removeChild(Episode child) {
            for (List<Episode> episodes : children) {
                Iterator<Episode> episodeIterator = episodes.iterator();
                while (episodeIterator.hasNext()) {
                    Episode next = episodeIterator.next();
                    if (next.equals(child)){
                        episodeIterator.remove();
                    }
                }
            }
            // remove empty groups
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                if (getChildrenCount(i) == 0){
                    children.remove(i);
                    shows.remove(i);
                }
            }

        }

        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }

        public List getGroupChildren(int groupPosition) {
            return children.get(groupPosition);
        }


        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final int gp = groupPosition;
            final ViewHolder holder;
            final UserShow userShow = (UserShow) getGroup(groupPosition);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.season, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.season_title);
                holder.unwatched = (TextView) convertView.findViewById(R.id.unwatched);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.season_check_box);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (userShow != null) {
                holder.title.setText(userShow.getTitle());
                holder.unwatched.setVisibility(View.VISIBLE);
                holder.unwatched.setText(getActivity().getResources().getString(R.string.unwatched) + ": " + getChildrenCount(groupPosition));
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        userShow.setChecked(isChecked);
                    }
                });
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        boolean isChecked = checkBox.isChecked();
                        for (Episode e : (List<Episode>) getGroupChildren(gp)) {
                            e.setChecked(isChecked);
                        }
                        ActionBarActivity activity = (ActionBarActivity) getActivity();
                        if (mMode == null)
                            mMode = activity.startSupportActionMode(new CheckNewEpisodesActionMode());
                        adapter.notifyDataSetChanged();
                    }
                });
                holder.checkBox.setChecked(userShow.isChecked());
            }
            return convertView;
        }

        protected class ViewHolder {
            protected TextView title;
            protected TextView unwatched;
            protected CheckBox checkBox;
            protected TextView shortTitle;
            private TextView airDate;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Episode episode = (Episode) getChild(groupPosition, childPosition);
            final int gp = groupPosition;
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.episode, parent, false);
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

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        // TODO NPE HERE???
                        UserShow userShow = (UserShow) getGroup(gp);
                        episode.setChecked(isChecked);

                        // uncheck group if group was checked and child became unchecked
                        if (!isChecked && userShow.isChecked()) {
                            userShow.setChecked(isChecked);
                            adapter.notifyDataSetChanged();
                        }


                        boolean isAllEpisodesChecked = true;
                        for (Episode e : (List<Episode>) getGroupChildren(gp)) {
                            if (!e.isChecked()) {
                                isAllEpisodesChecked = false;
                                break;
                            }
                        }
                        if (isAllEpisodesChecked) {
                            userShow.setChecked(true);
                            notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionBarActivity activity = (ActionBarActivity) getActivity();
                    if (mMode == null)
                        mMode = activity.startSupportActionMode(new CheckNewEpisodesActionMode());
                }
            });


            holder.checkBox.setChecked(episode.isChecked());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    if (mMode == null) {
                        ActionBarActivity activity = (ActionBarActivity) getActivity();
                        mMode = activity.startSupportActionMode(new CheckNewEpisodesActionMode());
                    }
                }
            });

            convertView.setOnCreateContextMenuListener(null);

            return convertView;
        }


        private String composeShortTitle(Episode e) {
            int season = e.getSeasonNumber();
            int episode = e.getEpisodeNumber();
            return  ("s" + String.format("%1$02d", season) + "e" + String.format("%1$02d", episode));
        }

        public Object getGroup(int groupPosition) {
            return shows.get(groupPosition);
        }

        public int getGroupCount() {
            return shows.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        public boolean hasStableIds() {
            return true;
        }


    }


    private class CheckNewEpisodesTask extends BaseTask<Map<Integer, Boolean>> {

        List<Episode> toRemove = null;
        int j = 0;


        @Override
        public  Map<Integer, Boolean> doInBackground(Object... objects) {
            Map<Integer, String> paramsMap = new HashMap<Integer, String>();

            // episodes to remove from list
            toRemove = new ArrayList<Episode>();

            for (int i = 0; i < adapter.getGroupCount(); i++) {
                List<Episode> episodes = adapter.getGroupChildren(i);
                for (Episode e : episodes) {
                    if (e.isChecked()) {
                        String value = paramsMap.get(e.getShowId());
                        if (value == null)
                            paramsMap.put(e.getShowId(), e.getEpisodeId().toString());
                        else
                            paramsMap.put(e.getShowId(), value += "," + e.getEpisodeId().toString());
                        toRemove.add(e);
                    }
                }
            }
            // showId - result map
            final Map<Integer, Boolean> checkedShowsResults = new HashMap<Integer, Boolean>();

            for (Map.Entry<Integer, String> entry : paramsMap.entrySet()) {
                try {
                    final Integer showId = entry.getKey();
                    final String episodesIds = entry.getValue();
                    Thread t = new Thread() {
                        public void run() {
                            UserShow userShow = MyShows.getUserShow(showId);
                            // update user show watched episodes
                            if (userShow != null)
                                userShow.setWatchedEpisodes(userShow.getWatchedEpisodes() + episodesIds.split(",").length);
                            // push task in separate thread
                            boolean result = MyShowsClient.getInstance().syncAllShowEpisodes(showId, episodesIds, null);
                            checkedShowsResults.put(showId, result);
                        }
                    };
                    t.start();
                    // wait for threads
                    t.join();
                    j++;
                } catch (Exception e) {
                    this.exception = e;
                    e.printStackTrace();
                }

            }
            return checkedShowsResults;
        }

        @Override
        protected void onPostExecute(Map<Integer, Boolean> result) {
            if (exception == null) {

                boolean isAllSuccess = true;
                boolean isOneSuccess = false;

                for (Map.Entry<Integer, Boolean> entry : result.entrySet()) {
                    // if check show episodes is success
                    if (entry.getValue()) {
                        isOneSuccess = true;
                        for (Episode episode : getEpisodeByShowId(toRemove, entry.getKey())) {
                            adapter.removeChild(episode);
                        }
                    }else {
                        isAllSuccess = false;
                    }

                }

                if (isAdded()) {
                    int message = isAllSuccess ? R.string.changes_saved : R.string.changes_not_saved;
                    message = (isOneSuccess && !isAllSuccess) ? R.string.changes_not_all_saved : message;
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }

                if (mMode != null)
                    mMode.finish();
            }
        }
    }


    private List<Episode> getEpisodeByShowId(List<Episode> allEpisodes, Integer showId) {
        List<Episode> selected = new LinkedList<Episode>();
        for (Episode episode : allEpisodes) {
            if (episode.getShowId().equals(showId))
                selected.add(episode);
        }
        return selected;
    }

    private final class CheckNewEpisodesActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.new_episodes, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_save:
                    BaseTask task = new CheckNewEpisodesTask();
                    task.execute();
                    break;
                case R.id.action_rate:

                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            int rating = msg.arg1;
                            BaseTask task = new ChangeEpisodesRateTask();
                            task.setTaskListener(new TaskListener<Boolean>() {
                                @Override
                                public void onTaskComplete(Boolean result) {
                                    if (isAdded())
                                        Toast.makeText(getActivity(), result ? R.string.changes_saved : R.string.changes_not_saved, Toast.LENGTH_SHORT).show();
                                    if (mMode != null)
                                        mMode.finish();
                                }

                                @Override
                                public void onTaskFailed(Exception e) {

                                }
                            });
                            task.execute(rating);
                        }
                    };
                    RatingDialog rate = new RatingDialog(getActivity(), handler);
                    rate.setTitle(R.string.episode_rating);
                    rate.show();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mMode = null;
        }
    }


    public class ChangeEpisodesRateTask extends BaseTask<Boolean> {
        ArrayList<Episode> episodes = (ArrayList<Episode>) adapter.getAllChildrenAsList();

        @Override
        public Boolean doInBackground(Object... objects) {

            Integer ratio = (Integer) objects[0];

            StringBuilder checkedIds = new StringBuilder();
            for (Episode e : episodes)
                if (e.isChecked())
                    checkedIds.append(e.getEpisodeId() + ",");

            String checked = checkedIds.toString();
            if (checked.endsWith(",")) checked = checked.substring(0, checked.length() - 1);
            return MyShowsClient.getInstance().changeEpisodesRatio(ratio, checked);
        }

    }


}