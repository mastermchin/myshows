package ru.myshows.tasks;

import ru.myshows.activity.MyShows;
import ru.myshows.domain.Genre;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 10.07.12
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
public class GetGenresTask extends BaseTask<Map<Integer, Genre>> {


    @Override
    public Map<Integer, Genre> doWork(Object... objects) throws Exception {
        if (MyShows.allGenres != null) return MyShows.allGenres;
        Map<Integer, Genre> genres = MyShows.client.getGenresListAsMap();
        MyShows.allGenres = genres;
        return genres;
    }

    @Override
    public void onResult(Map<Integer, Genre> result) {

    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
