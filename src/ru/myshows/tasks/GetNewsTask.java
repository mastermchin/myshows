package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
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

    private NewsLoadingListener newsLoadingListener;

    public GetNewsTask(Context context) {
        super(context);
    }

    public GetNewsTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public Map<String, List<UserNews>> doWork(Object... objects) throws Exception {
        if (isForceUpdate)
            MyShows.news = null;
        Map<String, List<UserNews>> news = MyShows.news != null ? MyShows.news : MyShows.client.getNews();
        MyShows.news = news;
        return news;
    }

    @Override
    public void onResult(Map<String, List<UserNews>> result) {
        newsLoadingListener.onNewsLoaded(result);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }


    public static interface NewsLoadingListener {
        public void onNewsLoaded(Map<String, List<UserNews>> news);
    }

    public void setNewsLoadingListener(NewsLoadingListener newsLoadingListener) {
        this.newsLoadingListener = newsLoadingListener;
    }
}
