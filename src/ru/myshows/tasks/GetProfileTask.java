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

    private TaskListener taskListener;

    public GetProfileTask(Context context) {
        super(context);
    }

    public GetProfileTask(Context context, boolean forceUpdate) {
        super(context, forceUpdate);
    }

    @Override
    public Profile doWork(Object... objects) throws Exception {
        String login = (String) objects[0];
        if (isForceUpdate)
            MyShows.profiles.put(login, null);
        Profile profile = MyShows.profiles.get(login) != null ? MyShows.profiles.get(login) : MyShows.client.getProfile(login);
        MyShows.profiles.put(login, profile);
        return profile;
    }

    @Override
    public void onResult(Profile result) {
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
