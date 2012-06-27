package ru.myshows.activity;

import android.app.Application;
import android.content.Context;
import ru.myshows.client.MyShowsClient;
import ru.myshows.domain.UserShow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 14.09.2011
 * Time: 15:49:48
 * To change this template use File | Settings | File Templates.
 */
public class MyShows extends Application {


    private static Context context;
    private static MyShowsClient client;
    private static boolean isLoggedIn;

    private List<UserShow> userShows = null;
    private boolean isUserShowsChanged = false;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        client = MyShowsClient.getInstance();
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isUserShowsChanged() {
        return isUserShowsChanged;
    }

    public void setUserShowsChanged(boolean userShowsChanged) {
        isUserShowsChanged = userShowsChanged;
    }

    public List<UserShow> getUserShows() {
        return userShows;
    }

    public void setUserShows(List<UserShow> userShows) {
        this.userShows = userShows;
    }

    public UserShow getUserShow(Integer showId) {
        if (userShows == null) return null;
        if (showId == null) return null;
        for (UserShow us : userShows) {
            if (us.getShowId() != null && us.getShowId().equals(showId))
                return us;
        }
        return null;
    }

    public void addOrUpdateUserShow(UserShow show) {
        if (userShows == null) userShows = new ArrayList<UserShow>();
        boolean found = false;
        for (UserShow u : userShows) {
            if (u.getShowId().equals(show.getShowId())) {
                u.setWatchStatus(show.getWatchStatus());
                found = true;
                break;
            }
        }
        if (!found) {
            userShows.add(show);
        } else {

        }

    }

    public void clearShows() {
        if (userShows != null) userShows.clear();
        userShows = null;
    }

    public void removeUserShow(Integer showId) {
        if (userShows != null) {
            for (UserShow u : userShows) {
                if (u.getShowId().equals(showId)) {
                    userShows.remove(u);
                    break;
                }
            }
        }
    }

    public static Context getContext() {
        return context;
    }

    public static MyShowsClient getClient() {
        return client;
    }


}
