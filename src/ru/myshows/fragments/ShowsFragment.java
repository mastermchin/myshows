package ru.myshows.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.activity.ShowActivity;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.IShow;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.BaseTask;
import ru.myshows.tasks.GetShowsTask;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.Utils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:19:35
 * To change this template use File | Settings | File Templates.
 */
public class ShowsFragment extends Fragment implements  Taskable , GetShowsTask.ShowsLoadingListener{

    public static final int SHOWS_SEARCH = 1;
    public static final int SHOWS_TOP = 2;
    public static final int SHOWS_USER = 3;
    public static final int SHOWS_ALL = 4;


    private int action;
    private ListView  list;
    private ProgressBar progress;
    private LayoutInflater inflater;

    public ShowsFragment() {
    }

    public ShowsFragment(int action) {
        this.action = action;
    }


    public void setAction(int action) {
        this.action = action;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shows, container, false);
        list =     (ListView) view.findViewById(R.id.shows_list);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        this.inflater = inflater;
        return view;
    }

    @Override
    public void onShowsLoaded(List<IShow> shows) {
        list.setAdapter(populateAdapter(action, shows));
        progress.setVisibility(View.GONE);
        progress.setIndeterminate(false);
        list.setVisibility(View.VISIBLE);
    }


    @Override
    public void executeTask(){
        GetShowsTask task = new GetShowsTask(getActivity(), GetShowsTask.SHOWS_USER);
        task.setShowsLoadingListener(this);
        task.execute();
    }

    @Override
    public void executeUpdateTask() {
        GetShowsTask task = new GetShowsTask(getActivity(), true, GetShowsTask.SHOWS_USER);
        task.setShowsLoadingListener(this);
        list.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        task.execute();
    }

    public class ShowsAdapter extends ArrayAdapter<IShow> {

        private List<IShow> shows;
        private String section;
        private Context context;

        public ShowsAdapter(Context context, int textViewResourceId, List<IShow> shows, String section) {
            super(context, textViewResourceId, shows);
            this.context = context;
            this.shows = shows;
            this.section = section;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            View row = convertView;
            if (row == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = vi.inflate(R.layout.show_item, null);
            }

            IShow show = shows.get(position);
            if (show != null) {

                final ImageView image = (ImageView) row.findViewById(R.id.show_logo);

                ImageLoader.getInstance().displayImage(show.getImageUrl(), image, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted() {
                        image.setImageResource(R.drawable.ic_list_logo);
                        image.setScaleType(ImageView.ScaleType.CENTER);
                    }

                    @Override
                    public void onLoadingFailed(FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete() {
                        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    @Override
                    public void onLoadingCancelled() {

                    }
                });

                TextView title = (TextView) row.findViewById(R.id.show_name);
                title.setText(show.getTitle());

                ((RatingBar) row.findViewById(R.id.show_rating)).setRating(show.getRating().floatValue());

                if (show instanceof UserShow) {

                    if (show.getWatchStatus().equals(MyShowsApi.STATUS.watching) && ((UserShow) show).getTotalEpisodes() > ((UserShow) show).getWatchedEpisodes()) {
                        int unwatched = ((UserShow) show).getTotalEpisodes() - ((UserShow) show).getWatchedEpisodes();
                        TextView b = ((TextView) row.findViewById(R.id.unwatched_episodes));
                        b.setVisibility(View.VISIBLE);
                        b.setText(String.valueOf(unwatched));

                    }
                }

            }


            row.setOnClickListener(new View.OnClickListener() {
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
                    context.startActivity(intent);
                }
            });

            return row;
        }

    }



    public SectionedAdapter populateAdapter(int action, List<IShow> shows) {
        SectionedAdapter adapter = new SectionedAdapter(inflater);
        Resources res = getActivity().getResources();
        switch (action) {

            case SHOWS_SEARCH:
                String search = res.getString(R.string.search_results);
                adapter.addSection(search, new ShowsAdapter(getActivity(), R.layout.show_item, shows, search));
                break;
            case SHOWS_TOP:
                String top = res.getString(R.string.top);
                adapter.addSection(top, new ShowsAdapter(getActivity(), R.layout.show_item, shows, top));
                break;

            case SHOWS_ALL:
                String all = res.getString(R.string.all);
                adapter.addSection(all, new ShowsAdapter(getActivity(), R.layout.show_item, shows, all));
                break;
            case SHOWS_USER:
                String watching = res.getString(R.string.status_watching);
                List<IShow> watchingShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.watching);
                if (watchingShows.size() > 0)
                    adapter.addSection(watching + " (" + watchingShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, watchingShows, watching + " (" + watchingShows.size() + ")"));

                String willWatch = res.getString(R.string.status_will_watch);
                List<IShow> willWatchShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.later);
                if (willWatchShows.size() > 0)
                    adapter.addSection(willWatch + " (" + willWatchShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, willWatchShows, willWatch + " (" + willWatchShows.size() + ")"));

                String cancelled = res.getString(R.string.status_cancelled);
                List<IShow> cancelledShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.cancelled);
                if (cancelledShows.size() > 0)
                    adapter.addSection(cancelled + " (" + cancelledShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, cancelledShows, cancelled + " (" + cancelledShows.size() + ")"));

                String remove = res.getString(R.string.status_finished);
                List<IShow> finishedShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.finished);
                if (finishedShows.size() > 0)
                    adapter.addSection(remove + " (" + finishedShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, finishedShows, remove + " (" + finishedShows.size() + ")"));
                System.out.println("Populate shows adapter complete!");
                break;
            default:
                adapter.notifyDataSetChanged();
        }
        return adapter;
    }




    //    @Override
//    public void onResume() {
//        super.onResume();
////        if (app.isUserShowsChanged() && lastAction == SHOWS_USER) {
////            new GetShowsTask(getActivity()).execute(lastAction, search);
////            app.setUserShowsChanged(false);
////        }
//    }





}
