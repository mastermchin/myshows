package ru.myshows.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.activity.ShowActivity;
import ru.myshows.activity.ShowsActivity;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.*;
import ru.myshows.tasks.ChangeShowStatusTask;
import ru.myshows.tasks.GetShowsTask;
import ru.myshows.tasks.TaskListener;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.Settings;
import ru.myshows.util.ShowsComparator;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:19:35
 * To change this template use File | Settings | File Templates.
 */
public class ShowsFragment extends Fragment implements Taskable, Searchable, TaskListener<List<IShow>> {

    public static final int SHOWS_SEARCH = 1;
    public static final int SHOWS_TOP = 2;
    public static final int SHOWS_USER = 3;
    public static final int SHOWS_ALL = 4;


    private int action;
    private ListView list;
    private ProgressBar progress;
    private SectionedAdapter adapter;
    private TextView message;

    public void setAction(int action) {
        this.action = action;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED && action == SHOWS_USER) {
            list.setAdapter(populateAdapter(action, MyShows.userShows));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shows, container, false);
        list = (ListView) view.findViewById(R.id.shows_list);
        progress = (ProgressBar) view.findViewById(R.id.progress_shows);
        message = (TextView) view.findViewById(R.id.message);
        if (savedInstanceState != null)
            action = savedInstanceState.getInt("action");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        executeTask();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_shows, menu);
        menu.findItem(R.id.action_add_show).setVisible(action == SHOWS_USER);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_show:
                Intent intent = new Intent(getActivity(), ShowsActivity.class);
                intent.putExtra("action", ShowsFragment.SHOWS_TOP);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTaskComplete(List<IShow> result) {
        if (isAdded()){
            if (result != null && !result.isEmpty()){
                list.setAdapter(populateAdapter(action, result));
                message.setVisibility(View.GONE);
            }else{
                list.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(action == SHOWS_USER ? R.string.empty_show_list: R.string.empty_search);
            }

            progress.setVisibility(View.GONE);
            progress.setIndeterminate(false);
            list.setVisibility(View.VISIBLE);
        }

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


    @Override
    public void executeTask() {
        action = getArguments().getInt("action");
        String search = getArguments().getString("search");
        GetShowsTask task = new GetShowsTask(getActivity(), action);
        task.setTaskListener(this);
        if (search != null)
            task.execute(search);
        else
            task.execute();
    }

    @Override
    public void executeUpdateTask() {
        GetShowsTask task = new GetShowsTask(getActivity(), true, action);
        task.setTaskListener(this);
        list.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        task.execute();
    }

    public class ShowsAdapter extends ArrayAdapter<IShow> {

        private List<? extends IShow> shows;
        private Context context;

        public ShowsAdapter(Context context, int textViewResourceId, List shows) {
            super(context, textViewResourceId, shows);
            this.context = context;
            this.shows = shows;
        }

        @Override
        public int getCount() {
            if (shows == null || shows.isEmpty()) return 0;
            return shows.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.show_item, null);
                holder = new ViewHolder();
                holder.logo = (ImageView) convertView.findViewById(R.id.show_logo);
                holder.title = (TextView) convertView.findViewById(R.id.show_name);
                holder.rating = (RatingBar) convertView.findViewById(R.id.show_rating);
                holder.unwatched = (TextView) convertView.findViewById(R.id.unwatched_episodes);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

           final IShow show = shows.get(position);
            if (show != null) {

                ImageLoader.getInstance().displayImage(show.getImageUrl(), holder.logo);

                holder.title.setText(show.getTitle());
                holder.rating.setRating(/*userShow != null ? userShow.getRating().floatValue() : */show.getRating().floatValue());

                if (show instanceof UserShow) {

                    if (show.getWatchStatus().equals(MyShowsApi.STATUS.watching)) {
                        int unwatched = getUnwatchedEpisodesCount(show.getShowId());
                        if (unwatched > 0)
                            holder.unwatched.setText(unwatched + "");
                    }
                }

                try {
                    if (action == SHOWS_TOP) {

                        final UserShow userShow = MyShows.getUserShow(show.getShowId());
                        if (userShow != null)
                            holder.unwatched.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_eye_red), null);
                        else
                            holder.unwatched.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_eye_gray), null);


                        holder.unwatched.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int selected = 4; // last status from statuses arrat = removed;
                                if (userShow != null && userShow.getWatchStatus() != null){
                                     selected = userShow.getWatchStatus().ordinal();
                                }


                                new AlertDialog.Builder(getActivity())
                                        .setSingleChoiceItems(R.array.show_statuses, selected, null)
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                                final MyShowsApi.STATUS selectedStatus =  MyShowsApi.STATUS.values()[selectedPosition];

                                                ChangeShowStatusTask task = new ChangeShowStatusTask(getActivity());
                                                task.setTaskListener(new TaskListener() {
                                                    @Override
                                                    public void onTaskComplete(Object result) {
                                                        Toast.makeText(getActivity(), R.string.changes_saved, Toast.LENGTH_SHORT).show();

                                                        if (userShow !=null){
                                                            userShow.setWatchStatus(selectedStatus);
                                                            if (selectedStatus == MyShowsApi.STATUS.remove)
                                                                MyShows.userShows.remove(userShow);
                                                        }else {
                                                            if (MyShows.userShows == null)
                                                                MyShows.userShows = new ArrayList<UserShow>();
                                                            MyShows.userShows.add(new UserShow(show, selectedStatus));
                                                        }
                                                        adapter.notifyDataSetChanged();
                                                        adapter.getSections().get(0).adapter.notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onTaskFailed(Exception e) {
                                                        Toast.makeText(getActivity(), R.string.changes_not_saved, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                task.execute(show.getShowId(),selectedStatus);
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, null)
                                        .show();
                            }
                        });
                    }

                } catch (Exception e) {

                }

            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //IShow show = adapter.getItem(pos, section);
                    IShow show = getItem(pos);
                    Intent intent = new Intent();
                    intent.putExtra("showId", show.getShowId());
                    intent.putExtra("title", show.getTitle());
                    intent.putExtra("watchStatus", show.getWatchStatus());
                    intent.putExtra("yoursRating", show.getYoursRating());
                    intent.setClass(context, ShowActivity.class);
                    //context.startActivity(intent);
                    ShowsFragment.this.startActivityForResult(intent, 1);
                }
            });

            return convertView;
        }


        public List<? extends IShow> getShows() {
            return shows;
        }

        private int getUnwatchedEpisodesCount(Integer showId) {
            if (MyShows.newEpisodes == null) return 0;
            Map<Integer, List<Episode>> episodesByShows = new HashMap<Integer, List<Episode>>();
            int count = 0;
            for (Episode e : MyShows.newEpisodes) {
                // exclude special episodes
                if (e.getEpisodeNumber() == 0)
                    continue;
                if (e.getShowId().equals(showId))
                    count++;
            }
            return count;
        }
    }


    public static class ViewHolder {
        public ImageView logo;
        public TextView title;
        public RatingBar rating;
        public TextView unwatched;
        public ImageView eye;
    }

    public SectionedAdapter populateAdapter(int action, List<? extends IShow> shows) {
        //adapter = new SectionedAdapter(getActivity(), R.layout.header, null);

        List<SectionedAdapter.Section> sectionList = new ArrayList<SectionedAdapter.Section>();

        Resources res = getActivity().getResources();
        switch (action) {

            case SHOWS_SEARCH:
                String search = res.getString(R.string.search_results);
                sectionList.add(new SectionedAdapter.Section(search, new ShowsAdapter(getActivity(), R.layout.show_item, shows)));
                break;
            case SHOWS_TOP:
                String top = res.getString(R.string.top);
                sectionList.add(new SectionedAdapter.Section(top, new ShowsAdapter(getActivity(), R.layout.show_item, shows)));
                break;

            case SHOWS_ALL:
                String all = res.getString(R.string.all);
                sectionList.add(new SectionedAdapter.Section(all, new ShowsAdapter(getActivity(), R.layout.show_item, shows)));
                break;
            case SHOWS_USER:
                //  shows = MyShows.userShows != null ? MyShows.userShows : shows;
                ShowsComparator sc = new ShowsComparator("title");


                String watching = res.getString(R.string.status_watching);
                List<IShow> watchingShows = getByWatchStatus(shows, MyShowsApi.STATUS.watching);
                Collections.sort(watchingShows, sc);
                if (watchingShows.size() > 0)
                    sectionList.add(new SectionedAdapter.Section(watching + " (" + watchingShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, watchingShows)));

                String willWatch = res.getString(R.string.status_will_watch);
                List<IShow> willWatchShows = getByWatchStatus(shows, MyShowsApi.STATUS.later);
                Collections.sort(willWatchShows, sc);
                if (willWatchShows.size() > 0)
                    sectionList.add(new SectionedAdapter.Section(willWatch + " (" + willWatchShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, willWatchShows)));


                String cancelled = res.getString(R.string.status_cancelled);
                List<IShow> cancelledShows = getByWatchStatus(shows, MyShowsApi.STATUS.cancelled);
                Collections.sort(cancelledShows, sc);
                if (cancelledShows.size() > 0)
                    sectionList.add(new SectionedAdapter.Section(cancelled + " (" + cancelledShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, cancelledShows)));


                String remove = res.getString(R.string.status_finished);
                List<IShow> finishedShows = getByWatchStatus(shows, MyShowsApi.STATUS.finished);
                Collections.sort(finishedShows, sc);
                if (finishedShows.size() > 0)
                    sectionList.add(new SectionedAdapter.Section(remove + " (" + finishedShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, finishedShows)));
                break;


        }
        adapter = new SectionedAdapter(getActivity(), R.layout.header, sectionList);
        adapter.notifyDataSetChanged();
        return adapter;
    }


    public static List<IShow> getByWatchStatus(List<? extends IShow> shows, MyShowsApi.STATUS status) {
        List<IShow> list = new ArrayList<IShow>();
        if (shows == null) return list;
        for (IShow show : shows) {
            if (show.getWatchStatus().equals(status)) {
                list.add(show);
            }
        }
        return list;

    }


    @Override
    public ArrayAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("action", action);
    }
}
