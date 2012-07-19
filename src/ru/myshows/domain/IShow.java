package ru.myshows.domain;

import ru.myshows.api.MyShowsApi;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 11.07.2011
 * Time: 17:41:23
 * To change this template use File | Settings | File Templates.
 */
public interface IShow extends Serializable, Filterable{

    public Integer getShowId();
    public String getTitle();
    public Double getRating();
    public Double getYoursRating();
    public MyShowsApi.STATUS getWatchStatus();
    public String getImageUrl();

    @Override
    String getFilterString();
}
