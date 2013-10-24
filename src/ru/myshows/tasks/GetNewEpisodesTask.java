package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.api.MyShowsClient;
import ru.myshows.domain.Episode;
import ru.myshows.domain.UserNews;
import ru.myshows.fragments.NewEpisodesFragment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 30.06.12
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class GetNewEpisodesTask extends BaseTask<List<Episode>> {

    public GetNewEpisodesTask(Context context) {
        super(context);
    }

    public GetNewEpisodesTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public List<Episode> doInBackground(Object... objects){
        MyShows.newEpisodes = (isForceUpdate || MyShows.newEpisodes == null) ? client.getUnwatchedEpisodes(): MyShows.newEpisodes;
        return  MyShows.newEpisodes;
    }

}
