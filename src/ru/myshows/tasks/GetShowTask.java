package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.domain.Episode;
import ru.myshows.domain.Genre;
import ru.myshows.domain.Show;
import ru.myshows.domain.WatchedEpisode;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.07.12
 * Time: 1:36
 * To change this template use File | Settings | File Templates.
 */
public class GetShowTask extends BaseTask<Show> {


    public GetShowTask(Context context) {
        super(context);
    }

    @Override
    public Show doInBackground(Object... objects) {
        int showId = (Integer) objects[0];
        Show show = client.getShowInfo(showId);
        // if show information unavailable
        if (show == null)
            return show;

        populateGenres(show, client.getGenresListAsMap());
        populateWatchedEpisodes(show, client.getSeenEpisodes(showId));

        if (MyShows.userShows == null)
            MyShows.userShows =  client.getShows();

        return show;
    }




    private void populateWatchedEpisodes(Show show, List<WatchedEpisode> episodes) {
        if (episodes == null || episodes.size() == 0) return;
        Iterator<Episode> i = show.getEpisodes().iterator();
        while (i.hasNext()) {
            Episode e = i.next();
            Iterator<WatchedEpisode> iter = episodes.iterator();
            while (iter.hasNext()) {
                WatchedEpisode we = iter.next();
                if (e.getEpisodeId().equals(we.getWatchedId())) {
                    e.setChecked(true);
                    break;
                }
            }

        }
    }


    private void populateGenres(Show show, Map<Integer, Genre> allGenres) {
        Collection<Integer> showGenres = show.getGenresIds();
        String genresString = "";
        if (showGenres != null) {
            for (Iterator<Integer> iter = showGenres.iterator(); iter.hasNext(); ) {
                Integer genreId = iter.next();
                genresString += " " + allGenres.get(genreId).getTitle();
            }
        }
        show.setGenres(genresString);

    }


    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }
}
