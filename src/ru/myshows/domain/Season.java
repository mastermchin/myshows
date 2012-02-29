package ru.myshows.domain;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 03.08.2011
 * Time: 18:35:31
 * To change this template use File | Settings | File Templates.
 */
public class Season {

    private String title;
    private boolean isChecked;

    public Season(String title, boolean checked) {
        this.title = title;
        isChecked = checked;
    }

    public Season() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

