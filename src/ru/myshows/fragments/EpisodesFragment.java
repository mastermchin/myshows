package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.Episode;
import ru.myshows.domain.Season;
import ru.myshows.domain.Show;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.BaseTask;
import ru.myshows.tasks.GetNewEpisodesTask;
import ru.myshows.util.EpisodeComparator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.07.12
 * Time: 1:44
 * To change this template use File | Settings | File Templates.
 */
public class EpisodesFragment extends SherlockFragment {

    private Show show;
    private LayoutInflater inflater;
    private ExpandableListView episodesList;
    private MyExpandableListAdapter adapter;
    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private ActionMode mMode;

    public EpisodesFragment(Show show) {
        this.show = show;
    }



    public void refresh(Show show){
        Collection<Episode> episodes = show.getEpisodes();
        //exclude specials
        Iterator<Episode> iterator = episodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getEpisodeNumber() == 0)
                iterator.remove();
        }
        Episode o = (Episode) Collections.max(episodes, new EpisodeComparator());
        adapter = new MyExpandableListAdapter(episodes, o.getSeasonNumber());
        episodesList.setAdapter(adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.episodes, container, false);
        episodesList = (ExpandableListView) view.findViewById(R.id.episodes_list);
        refresh(show);
        return view;
    }



    public class CheckEpisodesTask extends BaseTask<Boolean> {
        ArrayList<Episode> episodes = (ArrayList<Episode>) adapter.getAllChildrenAsList();

        @Override
        public Boolean doWork(Object... objects) throws Exception {

            StringBuilder checkedIds = new StringBuilder();
            StringBuilder uncheckedIds = new StringBuilder();
            for (Episode e : episodes) {
                if (e.isChecked()) checkedIds.append(e.getEpisodeId() + ",");
                if (!e.isChecked()) uncheckedIds.append(e.getEpisodeId() + ",");
            }

            String checked = checkedIds.toString();
            if (checked.endsWith(",")) checked = checked.substring(0, checked.length() - 1);
            String unchecked = uncheckedIds.toString();
            if (unchecked.endsWith(",")) unchecked = unchecked.substring(0, unchecked.length() - 1);
            boolean result = MyShows.client.syncAllShowEpisodes(show.getShowId(), checked, unchecked);
            if (result) {
                // update watched episodes in cache
                UserShow us = MyShows.getUserShow(show.getShowId());
                if (us != null) {
                    us.setWatchedEpisodes(checked.split(",").length);
                }
            }
            return result;
        }

        @Override
        public void onResult(Boolean result) {
            Toast.makeText(getActivity(), result ? R.string.changes_saved : R.string.changes_not_saved, Toast.LENGTH_SHORT).show();
            if (result){
                new GetNewEpisodesTask(getActivity(), true).execute();
            }

        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    }


    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Season> groups = new ArrayList<Season>();
        private ArrayList<ArrayList<Episode>> children = new ArrayList<ArrayList<Episode>>();


        public MyExpandableListAdapter(Collection<Episode> eps, int totalSeasons) {
            if (eps == null || eps.isEmpty())
                return;

            for (int i = totalSeasons; i >= 1; i--) {
                boolean isAllEpisodesWatched = true;
                ArrayList<Episode> seasonEpisodes = new ArrayList<Episode>();
                for (Iterator<Episode> iter = eps.iterator(); iter.hasNext(); ) {
                    Episode e = iter.next();
                    if (e.getSeasonNumber() == i) {
                        seasonEpisodes.add(e);
                        if (!e.isChecked())
                            isAllEpisodesWatched = false;
                    }
                }
                groups.add(new Season(getResources().getString(R.string.season) + " " + i, isAllEpisodesWatched));
                Collections.sort(seasonEpisodes, new EpisodeComparator("episode"));
                children.add(seasonEpisodes);

            }


        }

        public Object getAllChildrenAsList() {
            ArrayList<Episode> episodes = new ArrayList<Episode>();
            for (Iterator<ArrayList<Episode>> i = children.iterator(); i.hasNext(); ) {
                ArrayList<Episode> list = i.next();
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

        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }

        public List getGroupChildren(int groupPosition) {
            return children.get(groupPosition);
        }


        public void checkAll() {
            for (Season s : groups) {
                s.setChecked(true);
            }
            for (Episode e : (List<Episode>) getAllChildrenAsList()) {
                if (e.getAirDate() != null && e.getAirDate().before(new Date()))
                    e.setChecked(true);
            }
            adapter.notifyDataSetChanged();
        }

        public void uncheckAll() {
            for (Season s : groups) {
                s.setChecked(false);
            }
            for (Episode e : (List<Episode>) getAllChildrenAsList()) {
                e.setChecked(false);
            }
            adapter.notifyDataSetChanged();
        }

        public boolean isAllChecked(){
            for (int i=0; i < getGroupCount(); i++){
                Season s = (Season)getGroup(i);
                if (!s.isChecked())
                    return false;
                for (int j = 0; j < getChildrenCount(i); j++){
                    Episode e = (Episode) getChild(i, j);
                    if (e.getAirDate() != null && e.getAirDate().before(new Date()) && !e.isChecked())
                        return false;
//                    if (!e.isChecked())
//                        return false;
                }
            }
            return true;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final int gp = groupPosition;
            final ViewHolder holder;
            final Season season = (Season) getGroup(groupPosition);
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

            holder.title.setText(season.getTitle());
            holder.unwatched.setVisibility(View.GONE);
            // show checkboxes only if user is logged in
            if (MyShows.isLoggedIn  &&  MyShows.getUserShow(show.getShowId()) != null) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        season.setChecked(isChecked);
                    }
                });
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        boolean isChecked = checkBox.isChecked();
                        for (Episode e : (List<Episode>) getGroupChildren(gp)) {
                            if (e.getAirDate() != null && e.getAirDate().before(new Date()))
                                e.setChecked(isChecked);
                        }
                        SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
                        if (mMode == null)
                            mMode = activity.startActionMode(new SaveEpisodesActionMode());
                        adapter.notifyDataSetChanged();
                    }
                });
                holder.checkBox.setChecked(season.isChecked());
            }else {
                holder.checkBox.setVisibility(View.INVISIBLE);
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

            boolean isEpisodesExists = episode.getAirDate() != null && episode.getAirDate().before(new Date());

            holder.title.setText(episode.getTitle());
            holder.shortTitle.setText(episode.getShortName() != null ? episode.getShortName() : "");
            holder.airDate.setText(episode.getAirDate() != null ? df.format(episode.getAirDate()) : "unknown");

            // show checkboxes only if user is logged in
            if (MyShows.isLoggedIn && MyShows.getUserShow(show.getShowId()) != null && isEpisodesExists) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Season season = (Season) getGroup(gp);
                        episode.setChecked(isChecked);
                        if (!isChecked && season.isChecked()) {
                            season.setChecked(isChecked);
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
                            season.setChecked(true);
                            notifyDataSetChanged();
                        }


                    }
                });

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
                        if (mMode == null)
                            mMode = activity.startActionMode(new SaveEpisodesActionMode());
                    }
                });


                holder.checkBox.setChecked(episode.isChecked());

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    }
                });
            }else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }


            return convertView;
        }

        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        public int getGroupCount() {
            return groups.size();
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


    private final class SaveEpisodesActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Used to put dark icons on light action bar
            menu.add(0, 1, 1, R.string.save).setIcon(R.drawable.ic_save).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(0, 2, 2, R.string.menu_check_all).setIcon(R.drawable.ic_check_all).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(0, 3, 3, R.string.episode_rating).setIcon(R.drawable.ic_rating_important).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
            switch (item.getItemId()) {
                case 1:
                    new CheckEpisodesTask().execute();
                    mode.finish();
                    break;
                case 2:
                    if (adapter.isAllChecked())
                        adapter.uncheckAll();
                    else
                        adapter.checkAll();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mMode = null;
        }
    }

    public MyExpandableListAdapter getAdapter() {
        return adapter;
    }
}
