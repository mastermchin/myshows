package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.domain.Show;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.07.12
 * Time: 1:36
 * To change this template use File | Settings | File Templates.
 */
public class GetShowTask extends BaseTask<Show> {

    private ShowLoadingListener showLoadingListener;

    public GetShowTask(Context context) {
        super(context);
    }

    public GetShowTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public Show doWork(Object... objects) throws Exception {
        int showId = (Integer) objects[0];
        Show show = MyShows.client.getShowInfo(showId);
        return show;
    }

    @Override
    public void onResult(Show result) {
         showLoadingListener.onShowLoaded(result);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void setShowLoadingListener(ShowLoadingListener showLoadingListener) {
        this.showLoadingListener = showLoadingListener;
    }

    public interface ShowLoadingListener{
        public boolean onShowLoaded(Show show);
    }

}
