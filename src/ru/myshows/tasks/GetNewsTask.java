package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.api.MyShowsClient;
import ru.myshows.domain.UserNews;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 01.07.12
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
public class GetNewsTask extends BaseTask<Map<String, List<UserNews>>> {


    public GetNewsTask(Context context) {
        super(context);
    }

    public GetNewsTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public Map<String, List<UserNews>> doInBackground(Object... objects)  {
        MyShows.news = (isForceUpdate || MyShows.news == null) ? client.getNews() : MyShows.news;
        return MyShows.news;
    }

}
