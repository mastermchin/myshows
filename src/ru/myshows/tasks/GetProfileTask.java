package ru.myshows.tasks;

import android.content.Context;
import ru.myshows.activity.MyShows;
import ru.myshows.domain.Profile;

/**
 * Created by IntelliJ IDEA.
 * User: Demo
 * Date: 03.07.12
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class GetProfileTask extends BaseTask<Profile> {


    public GetProfileTask(Context context) {
        super(context);
    }


    @Override
    public Profile doInBackground(Object... objects)  {
        String login = (String) objects[0];
        return client.getProfile(login);
    }

}
