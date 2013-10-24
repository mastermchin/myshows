package ru.myshows.tasks;

import android.content.Context;
import android.os.AsyncTask;
import ru.myshows.api.MyShowsClient;
import ru.myshows.util.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 16.03.12
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseTask<T> extends AsyncTask<Object, Void, T> {

    protected Context context;
    protected Exception exception;
    protected boolean isForceUpdate;
    protected TaskListener taskListener;
    protected MyShowsClient client = MyShowsClient.getInstance();

    protected BaseTask() {
    }

    public BaseTask(Context context) {
        this.context = context;
    }

    protected BaseTask(Context context, boolean forceUpdate) {
        this.context = context;
        this.isForceUpdate = forceUpdate;
    }

    @Override
    protected void onPreExecute() {
        if (!Utils.isInternetAvailable(context)) {
            cancel(true);
            if (taskListener != null)
                taskListener.onTaskFailed(new Exception("No Internet available"));
        }
    }


    @Override
    protected void onPostExecute(T result) {
        if (taskListener != null) {
            if (exception == null)
                taskListener.onTaskComplete(result);
            else
                taskListener.onTaskFailed(exception);

        }
    }

    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }


}
