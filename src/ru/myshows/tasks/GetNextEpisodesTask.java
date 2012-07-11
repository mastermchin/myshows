package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
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

    private NextEpisodesLoadingListener nextEpisodesLoadingListener;

    public GetNextEpisodesTask(Context context) {
        super(context);
    }

    public GetNextEpisodesTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public List<Episode> doWork(Object... objects) throws Exception {
        if (isForceUpdate)
            MyShows.nextEpisodes = null;
        List<Episode> nextEpisodes = MyShows.nextEpisodes != null ? MyShows.nextEpisodes : MyShows.client.getNextEpisodes();
        MyShows.newEpisodes = nextEpisodes;
        return nextEpisodes;
    }

    @Override
    public void onResult(List<Episode> result) {
           nextEpisodesLoadingListener.onNextEpisodesLoaded(result);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void setNextEpisodesLoadingListener(NextEpisodesLoadingListener nextEpisodesLoadingListener) {
        this.nextEpisodesLoadingListener = nextEpisodesLoadingListener;
    }

    public interface NextEpisodesLoadingListener{

        public void onNextEpisodesLoaded(List<Episode> episodes);

    }

}
