package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.domain.IShow;
import ru.myshows.util.ShowsComparator;

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

    private ShowsLoadingListener showsLoadingListener;
    private int action;

    public GetShowsTask(Context context) {
        super(context);
    }


    public GetShowsTask(Context context, int action) {
        super(context);
        this.action = action;
    }

    public GetShowsTask(Context context, boolean forceUpdate, int action) {
        super(context, forceUpdate);
        this.action = action;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public List<IShow> doWork(Object... objects) throws Exception {
        if (!isOnline){
            this.cancel(true);
            return null;
        }

        List shows = null;
        switch (action) {
            case SHOWS_SEARCH:
                String query = (String) objects[1];
                shows = MyShows.client.search(query);
                break;
            case SHOWS_TOP:
                if (isForceUpdate)
                    MyShows.topShows = null;
                shows = MyShows.topShows != null ? MyShows.topShows : MyShows.client.getTopShows(null);
                Collections.sort(shows, new ShowsComparator());
                break;
            case SHOWS_ALL:
                if (isForceUpdate)
                    MyShows.allShows  = null ;
                shows = MyShows.allShows != null ? MyShows.allShows : MyShows.client.getTopShows(null);
                Collections.sort(shows, new ShowsComparator("title"));
                break;
            case SHOWS_USER:
                if (isForceUpdate)
                    MyShows.userShows  = null;
                shows = MyShows.userShows != null ? MyShows.userShows:  MyShows.client.getShows();
                MyShows.userShows = shows;
                break;

        }
        return shows;
    }

    @Override
    public void onResult(List<IShow> shows) {
        showsLoadingListener.onShowsLoaded(shows);

    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }


    public void setShowsLoadingListener(ShowsLoadingListener showsLoadingListener) {
        this.showsLoadingListener = showsLoadingListener;
    }



    public interface ShowsLoadingListener{

        public void onShowsLoaded(List<IShow> shows);

    }
}
