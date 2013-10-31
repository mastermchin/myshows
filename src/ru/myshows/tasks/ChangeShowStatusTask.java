package ru.myshows.tasks;

import android.content.Context;
import android.widget.Toast;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsApi;
import ru.myshows.api.MyShowsClient;
import ru.myshows.domain.UserShow;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dell
 * Date: 11/1/13
 * Time: 3:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeShowStatusTask extends BaseTask<Boolean> {


    public ChangeShowStatusTask(Context context) {
        super(context);
    }

    @Override
    public Boolean doInBackground(Object... objects)  {

        int showId = (Integer) objects[0];
        MyShowsApi.STATUS status = (MyShowsApi.STATUS) objects[1];
        return MyShowsClient.getInstance().changeShowStatus(showId, status);
    }


}
