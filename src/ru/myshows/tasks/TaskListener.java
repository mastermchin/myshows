package ru.myshows.tasks;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 16.07.12
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public interface TaskListener<T> {

    public void onTaskComplete(T result);
    public void onTaskFailed(Exception e);

}
