package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.activity.SectionedAdapter;
import ru.myshows.activity.ShowActivity;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.IShow;
import ru.myshows.domain.UserShow;
import ru.myshows.util.Utils;
import ru.myshows.util.ShowsComparator;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:19:35
 * To change this template use File | Settings | File Templates.
 */
public class ShowsFragment extends ListFragment {

    public static final int SHOWS_SEARCH = 1;
    public static final int SHOWS_TOP = 2;
    public static final int SHOWS_USER = 3;
    public static final int SHOWS_ALL = 4;


    private SectionedAdapter adapter;
    private LayoutInflater inflater;

    // private int action;

    public ShowsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.shows, container, false);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
////        if (app.isUserShowsChanged() && lastAction == SHOWS_USER) {
////            new GetShowsTask(getActivity()).execute(lastAction, search);
////            app.setUserShowsChanged(false);
////        }
//    }


    public static class ShowsAdapter extends ArrayAdapter<IShow> {
        private List<IShow> shows;
        private String section;
        private Context context;
        private SectionedAdapter adapter;

        public ShowsAdapter(Context context, int textViewResourceId, List<IShow> shows, String section) {
            super(context, textViewResourceId, shows);
            this.context = context;
            this.shows = shows;
            this.section = section;
            this.adapter = adapter;
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
                title.setTypeface(MyShows.font);
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

                    IShow show = (IShow)adapter.getItem(pos, section);
                    Intent intent = new Intent();
                    intent.putExtra("showId", show.getShowId());
                    intent.putExtra("watchStatus", show.getWatchStatus());
                    intent.putExtra("yoursRating", show.getYoursRating());
                    intent.setClass(context, ShowActivity.class);
                    context.startActivity(intent);
                }
            });

            return row;
        }
    }


    private class GetShowsTask extends AsyncTask {
        private ProgressDialog dialog;

        private GetShowsTask(Context context) {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();

        }


        @Override
        protected List doInBackground(Object... objects) {
            Integer action = (Integer) objects[0];
            List shows = null;
            switch (action) {
                case SHOWS_SEARCH:
                    String query = (String) objects[1];
                    shows = MyShows.getClient().search(query);
                    break;
                case SHOWS_TOP:
                    shows = MyShows.getTopShows() != null ? MyShows.getTopShows() : MyShows.getClient().getTopShows(null);
                    Collections.sort(shows, new ShowsComparator());
                    break;
                case SHOWS_ALL:
                    shows = MyShows.getAllShows() != null ? MyShows.getAllShows() : MyShows.getClient().getTopShows(null);
                    Collections.sort(shows, new ShowsComparator("title"));
                    break;
                case SHOWS_USER:
                    shows = MyShows.getUserShows() != null ? MyShows.getUserShows() : MyShows.getClient().getShows();
                    break;

            }
            return shows;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing()) this.dialog.dismiss();
            if (result != null) {
                adapter = new SectionedAdapter(inflater);
                populateAdapter(lastAction, (List<IShow>) result);
                setListAdapter(adapter);
            }

        }


    }


    public SectionedAdapter getAdapter() {
        return adapter;
    }

    public void populateAdapter(int action, List<IShow> shows) {
        switch (action) {
            case SHOWS_SEARCH:
                String search = getResources().getString(R.string.search_results);
                adapter.addSection(search, new ShowsAdapter(getActivity(), R.layout.show_item, shows, search));
                break;
            case SHOWS_TOP:
                String top = getResources().getString(R.string.top);
                adapter.addSection(top, new ShowsAdapter(getActivity(), R.layout.show_item, shows, top));
                break;

            case SHOWS_ALL:
                String all = getResources().getString(R.string.all);
                adapter.addSection(all, new ShowsAdapter(getActivity(), R.layout.show_item, shows, all));
                break;
            case SHOWS_USER:
                String watching = getResources().getString(R.string.status_watching);
                List<IShow> watchingShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.watching);
                if (watchingShows.size() > 0)
                    adapter.addSection(watching + " (" + watchingShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, watchingShows, watching + " (" + watchingShows.size() + ")"));

                String willWatch = getResources().getString(R.string.status_will_watch);
                List<IShow> willWatchShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.later);
                if (willWatchShows.size() > 0)
                    adapter.addSection(willWatch + " (" + willWatchShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, willWatchShows, willWatch + " (" + willWatchShows.size() + ")"));

                String cancelled = getResources().getString(R.string.status_cancelled);
                List<IShow> cancelledShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.cancelled);
                if (cancelledShows.size() > 0)
                    adapter.addSection(cancelled + " (" + cancelledShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, cancelledShows, cancelled + " (" + cancelledShows.size() + ")"));

                String remove = getResources().getString(R.string.status_finished);
                List<IShow> finishedShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.finished);
                if (finishedShows.size() > 0)
                    adapter.addSection(remove + " (" + finishedShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, finishedShows, remove + " (" + finishedShows.size() + ")"));
                System.out.println("Populate shows adapter complete!");
                break;
            default:
                adapter.notifyDataSetChanged();
        }
    }


}
