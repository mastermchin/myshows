package ru.myshows.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import ru.myshows.api.MyShowsApi;
import ru.myshows.client.MyShowsClient;
import ru.myshows.domain.IShow;
import ru.myshows.domain.UserShow;
import ru.myshows.util.ImageLoader;
import ru.myshows.util.MyShowsUtil;
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

    public static final int ACTION_SEARCH_SHOWS = 1;
    public static final int ACTION_GET_TOP_SHOWS = 2;
    public static final int ACTION_GET_USER_SHOWS = 3;
    public static final int ACTION_GET_ALL_SHOWS = 4;

    private SectionedAdapter adapter;
    private String search;
    private int lastAction;
    MyShowsClient client = MyShowsClient.getInstance();
    MyShows app;
    private LayoutInflater inflater;

    public ShowsFragment(String search) {
        this.search = search;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        ListView list = (ListView) inflater.inflate(R.layout.shows, container, false);
      //  getListView().setDivider(null);
       // getListView().setDividerHeight(0);

        //search = getBundleValue(getActivity().getIntent(), "search", null);

        if (search == null) lastAction = ACTION_GET_USER_SHOWS;
        else if (search.equals("top")) lastAction = ACTION_GET_TOP_SHOWS;
        else if (search.equals("all")) lastAction = ACTION_GET_ALL_SHOWS;
        else lastAction = ACTION_SEARCH_SHOWS;
        new GetShowsTask(getActivity()).execute(lastAction, search);
        return list;


    }


//    @Override
//    public void onResume() {
//        super.onResume();
////        if (app.isUserShowsChanged() && lastAction == ACTION_GET_USER_SHOWS) {
////            new GetShowsTask(getActivity()).execute(lastAction, search);
////            app.setUserShowsChanged(false);
////        }
//    }


    private class ShowsAdapter extends ArrayAdapter<IShow> {
        private List<IShow> shows;
        private String section;
        private ImageLoader imageLoader;
        private IShow last;

        private ShowsAdapter(Context context, int textViewResourceId, List<IShow> shows, String section) {
            super(context, textViewResourceId, shows);
            this.shows = shows;
            this.section = section;
            this.imageLoader = new ImageLoader(context);
            this.last = shows.get(shows.size() - 1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            View row = convertView;
            if (row == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = vi.inflate(R.layout.show_item, null);
            }

            IShow show = shows.get(position);
            if (show != null) {

                imageLoader.displayImage(show.getImageUrl(), (ImageView) row.findViewById(R.id.show_logo));

                ((TextView) row.findViewById(R.id.show_name)).setText(show.getTitle());
                ((RatingBar) row.findViewById(R.id.show_rating)).setRating(show.getRating().floatValue());

                if (show instanceof UserShow) {

                    if (show.getWatchStatus().equals(MyShowsApi.STATUS.watching) && ((UserShow) show).getTotalEpisodes() > ((UserShow) show).getWatchedEpisodes()) {
                        int unwatched = ((UserShow) show).getTotalEpisodes() - ((UserShow) show).getWatchedEpisodes();
                        TextView b = ((TextView) row.findViewById(R.id.unwatched_episodes));
                        b.setVisibility(View.VISIBLE);
                        b.setText(String.valueOf(unwatched));

                    }
                }

                // remove last divider in section
                if (show.equals(last)) {
                    ((ImageView) row.findViewById(R.id.divider)).setBackgroundDrawable(null);
                } else {
                    Drawable divider = getResources().getDrawable(R.drawable.divider_horizontal_dim_dark);
                    ((ImageView) row.findViewById(R.id.divider)).setBackgroundDrawable(divider);
                }
            }


            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    IShow show = (IShow) adapter.getItem(pos, section);
                    Intent intent = new Intent();
                    intent.putExtra("showId", show.getShowId());
                    intent.putExtra("watchStatus", show.getWatchStatus());
                    intent.putExtra("yoursRating", show.getYoursRating());
                    intent.setClass(getActivity(), ShowActivity.class);
                    startActivity(intent);
//                    ActivityStack activityStack = (ActivityStack) getParent();
//                    activityStack.push("ShowActivity", intent);
                }
            });

            return row;
        }
    }


    private class GetShowsTask extends AsyncTask {
        private int lastAction;
        private Context context;
        private ProgressDialog dialog;

        private GetShowsTask(Context context) {
            this.context = context;
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
                case ACTION_SEARCH_SHOWS:
                    lastAction = ACTION_SEARCH_SHOWS;
                    String query = (String) objects[1];
                    shows = client.search(query);
                    break;
                case ACTION_GET_TOP_SHOWS:
                    lastAction = ACTION_GET_TOP_SHOWS;
                    shows = client.getTopShows(null);
                    Collections.sort(shows, new ShowsComparator());
                    break;
                case ACTION_GET_ALL_SHOWS:
                    lastAction = ACTION_GET_ALL_SHOWS;
                    shows = client.getTopShows(null);
                    Collections.sort(shows, new ShowsComparator("title"));
                    break;
                case ACTION_GET_USER_SHOWS:

                    lastAction = ACTION_GET_USER_SHOWS;
                    String s = (String) objects[1];
                    if ((s != null && s.equals("update")) || app.getUserShows() == null) {
                        shows = client.getShows();
                        app.setUserShows(shows);
                    } else {
                        shows = app.getUserShows();
                    }
                    app.setUserShowsChanged(false);
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

    private String getBundleValue(Intent intent, String key, String defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key).toString();
    }


    private void populateAdapter(int action, List<IShow> shows) {
        switch (action) {
            case ACTION_SEARCH_SHOWS:
                String search = getResources().getString(R.string.search_results);
                adapter.addSection(search, new ShowsAdapter(getActivity(), R.layout.show_item, shows, search));
                break;
            case ACTION_GET_TOP_SHOWS:
                String top = getResources().getString(R.string.top);
                adapter.addSection(top, new ShowsAdapter(getActivity(), R.layout.show_item, shows, top));
                break;

            case ACTION_GET_ALL_SHOWS:
                String all = getResources().getString(R.string.all);
                adapter.addSection(all, new ShowsAdapter(getActivity(), R.layout.show_item, shows, all));
                break;
            case ACTION_GET_USER_SHOWS:
                String watching = getResources().getString(R.string.status_watching);
                List<IShow> watchingShows = MyShowsUtil.getByWatchStatus(shows, MyShowsApi.STATUS.watching);
                if (watchingShows.size() > 0)
                    adapter.addSection(watching + " (" + watchingShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, watchingShows, watching + " (" + watchingShows.size() + ")"));

                String willWatch = getResources().getString(R.string.status_will_watch);
                List<IShow> willWatchShows = MyShowsUtil.getByWatchStatus(shows, MyShowsApi.STATUS.later);
                if (willWatchShows.size() > 0)
                    adapter.addSection(willWatch + " (" + willWatchShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, willWatchShows, willWatch + " (" + willWatchShows.size() + ")"));

                String cancelled = getResources().getString(R.string.status_cancelled);
                List<IShow> cancelledShows = MyShowsUtil.getByWatchStatus(shows, MyShowsApi.STATUS.cancelled);
                if (cancelledShows.size() > 0)
                    adapter.addSection(cancelled + " (" + cancelledShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, cancelledShows, cancelled + " (" + cancelledShows.size() + ")"));

                String remove = getResources().getString(R.string.status_finished);
                List<IShow> finishedShows = MyShowsUtil.getByWatchStatus(shows, MyShowsApi.STATUS.finished);
                if (finishedShows.size() > 0)
                    adapter.addSection(remove + " (" + finishedShows.size() + ")", new ShowsAdapter(getActivity(), R.layout.show_item, finishedShows, remove + " (" + finishedShows.size() + ")"));
                System.out.println("Populate shows adapter complete!");
                break;
        }
    }


}
