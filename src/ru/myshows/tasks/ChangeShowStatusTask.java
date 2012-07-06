package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.api.MyShowsApi;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.07.12
 * Time: 2:13
 * To change this template use File | Settings | File Templates.
 */
public class ChangeShowStatusTask extends BaseTask<Boolean> {

    private ChangeShowStatusListener changeShowStatusListener;

    public ChangeShowStatusTask(Context context) {
        super(context);
    }

    public ChangeShowStatusTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public Boolean doWork(Object... objects) throws Exception {

        int showId = (Integer) objects[0];
        MyShowsApi.STATUS status = (MyShowsApi.STATUS) objects[1];
        return MyShows.client.changeShowStatus(showId, status);
    }

    @Override
    public void onResult(Boolean result) {
        changeShowStatusListener.onShowStatusChanged(result);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public interface ChangeShowStatusListener{

        public boolean onShowStatusChanged(boolean  result);

    }

}
