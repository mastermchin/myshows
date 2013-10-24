package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.api.MyShowsClient;
import ru.myshows.domain.Episode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.07.12
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class GetNextEpisodesTask extends BaseTask<List<Episode>> {


    public GetNextEpisodesTask(Context context) {
        super(context);
    }

    public GetNextEpisodesTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public List<Episode> doInBackground(Object... objects)  {
        MyShows.nextEpisodes = (isForceUpdate || MyShows.nextEpisodes == null) ? client.getNextEpisodes() : MyShows.nextEpisodes;
        if (MyShows.userShows == null)
            MyShows.userShows =  client.getShows();
        return MyShows.nextEpisodes;
    }

}
