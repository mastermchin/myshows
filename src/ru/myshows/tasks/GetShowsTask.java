package ru.myshows.tasks;

import android.content.Context;
import android.content.res.Resources;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.activity.SectionedAdapter;
import ru.myshows.activity.SectionedDemo;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.IShow;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.util.ShowsComparator;
import ru.myshows.util.Utils;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 29.06.12
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
public class GetShowsTask extends BaseTask<List<IShow>> {

    public static final int SHOWS_SEARCH = 1;
    public static final int SHOWS_TOP = 2;
    public static final int SHOWS_USER = 3;
    public static final int SHOWS_ALL = 4;

    private SectionedAdapter adapter;
    private int action;

    public GetShowsTask(Context context, SectionedAdapter adapter) {
        super(context);
    }

    @Override
    public List<IShow> doWork(Object... objects) throws Exception {
        if (!isOnline){
            this.cancel(true);
            return null;
        }

        action = (Integer) objects[0];
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
                shows = MyShows.getUserShows()!= null ? MyShows.getUserShows():  MyShows.getClient().getShows();
                break;

        }
        return shows;
    }

    @Override
    public void onResult(List<IShow> shows) {
        populateAdapter(action, shows);

    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }




    public void populateAdapter(int action, List<IShow> shows) {
        Resources res = context.getResources();
        switch (action) {

            case SHOWS_SEARCH:
                String search = res.getString(R.string.search_results);
                adapter.addSection(search, new ShowsFragment.ShowsAdapter(context, R.layout.show_item, shows, search, adapter));
                break;
            case SHOWS_TOP:
                String top = res.getString(R.string.top);
                adapter.addSection(top, new ShowsFragment.ShowsAdapter(context, R.layout.show_item, shows, top, adapter));
                break;

            case SHOWS_ALL:
                String all = res.getString(R.string.all);
                adapter.addSection(all, new ShowsFragment.ShowsAdapter(context, R.layout.show_item, shows, all, adapter));
                break;
            case SHOWS_USER:
                String watching = res.getString(R.string.status_watching);
                List<IShow> watchingShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.watching);
                if (watchingShows.size() > 0)
                    adapter.addSection(watching + " (" + watchingShows.size() + ")", new ShowsFragment.ShowsAdapter(context, R.layout.show_item, watchingShows, watching + " (" + watchingShows.size() + ")", adapter));

                String willWatch = res.getString(R.string.status_will_watch);
                List<IShow> willWatchShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.later);
                if (willWatchShows.size() > 0)
                    adapter.addSection(willWatch + " (" + willWatchShows.size() + ")", new ShowsFragment.ShowsAdapter(context, R.layout.show_item, willWatchShows, willWatch + " (" + willWatchShows.size() + ")", adapter));

                String cancelled = res.getString(R.string.status_cancelled);
                List<IShow> cancelledShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.cancelled);
                if (cancelledShows.size() > 0)
                    adapter.addSection(cancelled + " (" + cancelledShows.size() + ")", new ShowsFragment.ShowsAdapter(context, R.layout.show_item, cancelledShows, cancelled + " (" + cancelledShows.size() + ")", adapter));

                String remove = res.getString(R.string.status_finished);
                List<IShow> finishedShows = Utils.getByWatchStatus(shows, MyShowsApi.STATUS.finished);
                if (finishedShows.size() > 0)
                    adapter.addSection(remove + " (" + finishedShows.size() + ")", new ShowsFragment.ShowsAdapter(context, R.layout.show_item, finishedShows, remove + " (" + finishedShows.size() + ")", adapter));
                System.out.println("Populate shows adapter complete!");
                break;
            default:
                adapter.notifyDataSetChanged();
        }
    }
}
