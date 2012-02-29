package ru.myshows.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 16:20:24
 * To change this template use File | Settings | File Templates.
 */
public class WatchedEpisode extends JsonEvaluator implements JsonSerializable {

    private static final DateFormat DF = new SimpleDateFormat("dd.MM.yyyy");

    private Integer watchedId;
    private Date watchDate;

    public WatchedEpisode() {
    }

    public WatchedEpisode(JSONObject json) {
        try {
            this.watchDate = DF.parse(json.getString("watchDate"));
            this.watchedId = json.getInt("id");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getWatchedId() {
        return watchedId;
    }

    public void setWatchedId(Integer watchedId) {
        this.watchedId = watchedId;
    }

    public Date getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(Date watchDate) {
        this.watchDate = watchDate;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
        try {
            this.watchDate = DF.parse(getStringValue(json, "watchDate"));
            this.watchedId = getIntValue(json, "id");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "WatchedEpisode{" +
                "watchedId=" + watchedId +
                ", watchDate=" + watchDate +
                '}';
    }
}
