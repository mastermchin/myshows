package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.Episode;
import ru.myshows.domain.Season;
import ru.myshows.domain.Show;
import ru.myshows.domain.UserShow;
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
public class EpisodesFragment extends Fragment {

    private Show show;
    private LayoutInflater inflater;
    private ExpandableListView episodesList;
    private MyExpandableListAdapter adapter;
    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    public EpisodesFragment(Show show) {
        this.show = show;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       this.inflater = inflater;
        View view = inflater.inflate(R.layout.episodes, container, false);
        episodesList = (ExpandableListView) view.findViewById(R.id.episodes_list);

        Collection<Episode> episodes = show.getEpisodes();
        Episode o = (Episode) Collections.max(episodes, new EpisodeComparator());
        adapter = new MyExpandableListAdapter(episodes, o.getSeasonNumber());
        episodesList.setAdapter(adapter);

        return view;
    }


    View.OnClickListener saveButtonListener = new View.OnClickListener() {
        ProgressDialog dialog = null;
        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(getActivity(), msg.what, Toast.LENGTH_SHORT).show();
              //  removeSaveButton();
            }
        };

        Runnable checkEpisodesTask = new Runnable() {
            public void run() {
                ArrayList<Episode> episodes = (ArrayList<Episode>) adapter.getAllChildrenAsList();
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
                int message;
                if (result) {
                    message = R.string.changes_saved;
                    // update watched episodes in cache
                    UserShow us = MyShows.getUserShow(show.getShowId());
                    if (us != null) {
                        us.setWatchedEpisodes(checked.split(",").length);
                        // app.setUserShowsChanged(true);
                    }
                } else {
                    message = R.string.changes_not_saved;
                }
                handler.sendEmptyMessage(message);
            }

        };

        @Override
        public void onClick(View v) {
            //saveButton.setEnabled(false);
            dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.loading));
            handler.postDelayed(checkEpisodesTask, 1000);
        }

    };


    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Season> groups = new ArrayList<Season>();
        private ArrayList<ArrayList<Episode>> children = new ArrayList<ArrayList<Episode>>();


        public MyExpandableListAdapter(Collection<Episode> eps, int totalSeasons) {
            for (int i = 1; i <= totalSeasons; i++) {
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
                e.setChecked(true);
            }
            adapter.notifyDataSetChanged();
//            if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
//                isSaveButtonShowing = true;
//                addSaveButton();
//            }
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final int gp = groupPosition;
            final ViewHolder holder;
            final Season season = (Season) getGroup(groupPosition);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.season, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.season_title);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.season_check_box);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(season.getTitle());
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
                        e.setChecked(isChecked);
                    }
                    adapter.notifyDataSetChanged();
//                    if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
//                        isSaveButtonShowing = true;
//                        addSaveButton();
//                    }
                }
            });
            holder.checkBox.setChecked(season.isChecked());
            return convertView;
        }

        protected class ViewHolder {
            protected TextView title;
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
            holder.shortTitle.setText(episode.getShortName() != null ? episode.getShortName() : "");
            holder.airDate.setText(episode.getAirDate() != null ? df.format(episode.getAirDate()) : "unknown");

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
//                    if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
//                        isSaveButtonShowing = true;
//                        addSaveButton();
//                    }
                }
            });


            holder.checkBox.setChecked(episode.isChecked());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
//                    if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
//                        isSaveButtonShowing = true;
//                        addSaveButton();
//                    }
                }
            });

            convertView.setOnCreateContextMenuListener(null);

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
}
