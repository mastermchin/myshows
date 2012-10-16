package ru.myshows.activity;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import ru.myshows.api.MyShowsClient;
import ru.myshows.domain.*;
import ru.myshows.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 14.09.2011
 * Time: 15:49:48
 * To change this template use File | Settings | File Templates.
 */
public class MyShows extends Application {


    public static Context context;
    public static MyShowsClient client;
    public static boolean isLoggedIn;
    public static boolean isUserShowsChanged;
    public static Typeface font;


    public static List<UserShow> userShows;
    public static List<Show> topShows;
    public static List<Episode> newEpisodes;
    public static List<Episode> nextEpisodes;
    public static Map<String, List<UserNews>> news;
    public static Map<String,Profile> profiles = new HashMap<String, Profile>();
    public static Map<Integer, Genre> allGenres;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        client = MyShowsClient.getInstance();
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");


        // init image loader
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY)
                .discCache(new UnlimitedDiscCache( /*Utils.isSdAvailable() ? Utils.CACHE_DIR : */ getApplicationContext().getCacheDir()))
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);

    }



    public static  UserShow getUserShow(Integer showId) {
        if (showId == null)
            return null;
        if (userShows == null && isLoggedIn)
            userShows = client.getShows();
        if (userShows == null)
            return null;
        for (UserShow us : userShows) {
            if (us.getShowId() != null && us.getShowId().equals(showId))
                return us;
        }
        return null;
    }



     public static void invalidateUserData(){
         userShows = null;
         newEpisodes = null;
         nextEpisodes = null;
         news = null;
     }


}
