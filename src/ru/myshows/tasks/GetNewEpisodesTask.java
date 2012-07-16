package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
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

    private TaskListener taskListener;

    public GetNewEpisodesTask(Context context) {
        super(context);
    }

    public GetNewEpisodesTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public List<Episode> doWork(Object... objects) throws Exception {
        if (isForceUpdate)
            MyShows.newEpisodes = null;
        List<Episode> newEpisodes = MyShows.newEpisodes != null ? MyShows.newEpisodes : MyShows.client.getUnwatchedEpisodes();
        MyShows.newEpisodes = newEpisodes;
        return newEpisodes;
    }

    @Override
    public void onResult(List<Episode> result) {
        if (taskListener != null)
            taskListener.onTaskComplete(result);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        taskListener.onTaskFailed(e);
    }

    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }
}
