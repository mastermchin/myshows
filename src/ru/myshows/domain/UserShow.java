package ru.myshows.domain;

import org.json.JSONException;
import org.json.JSONObject;
import ru.myshows.api.MyShowsApi;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 16:18:30
 * To change this template use File | Settings | File Templates.
 */

public class UserShow extends JsonEvaluator implements IShow, JsonSerializable{

    private Integer showId;
    private String title;
    private String ruTitle;
    private Integer runtime;
    private String showStatus;
    private MyShowsApi.STATUS watchStatus;
    private Integer watchedEpisodes;
    private Integer totalEpisodes;
    private Double rating;
    private String imageUrl;
    private boolean isChecked;

    public UserShow() {
    }


    public UserShow(IShow show, MyShowsApi.STATUS status) {
        this.title = show.getTitle();
        this.rating = show.getYoursRating();
        this.showId = show.getShowId();
        this.imageUrl = show.getImageUrl();
        this.watchStatus = status;
    }

    public UserShow(Show show, MyShowsApi.STATUS status) {
        this.title = show.getTitle();
        this.ruTitle = show.getRuTitle();
        this.rating = show.getYoursRating();
        this.showId = show.getShowId();
        this.imageUrl = show.getImageUrl();
        this.totalEpisodes = show.getEpisodes().size();
        this.watchedEpisodes = 0;
        this.watchStatus = status;
    }

    public UserShow(JSONObject json) {
        try {
            this.showId = json.getInt("showId");
            this.title = json.getString("title");
            this.ruTitle = json.getString("ruTitle");
            this.runtime = json.getInt("runtime");
            this.showStatus = json.getString("showStatus");
            this.watchStatus = MyShowsApi.STATUS.valueOf(json.getString("watchStatus"));
            this.watchedEpisodes = json.getInt("watchedEpisodes");
            this.totalEpisodes = json.getInt("totalEpisodes");
            this.rating = json.getDouble("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFilterString() {
        return title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public String getTitle() {
        if (Locale.getDefault().getLanguage().equals("ru") &&
                getRuTitle() != null &&
                !getRuTitle().equals("null") &&
                getRuTitle().length() > 0)
            return getRuTitle();
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRuTitle() {
        return ruTitle;
    }

    public void setRuTitle(String ruTitle) {
        this.ruTitle = ruTitle;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public MyShowsApi.STATUS getWatchStatus() {
        return watchStatus;
    }

    public void setWatchStatus(MyShowsApi.STATUS watchStatus) {
        this.watchStatus = watchStatus;
    }

    public Integer getWatchedEpisodes() {
        return watchedEpisodes;
    }

    public void setWatchedEpisodes(Integer watchedEpisodes) {
        this.watchedEpisodes = watchedEpisodes;
    }

    public Integer getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(Integer totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public Double getRating() {
        return rating;
    }

    @Override
    public Double getYoursRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
        this.showId = getIntValue(json, "showId");
        this.title = getStringValue(json, "title");
        this.ruTitle = getStringValue(json, "ruTitle");
        this.runtime = getIntValue(json, "runtime");
        this.showStatus = getStringValue(json, "showStatus");
        this.watchStatus = MyShowsApi.STATUS.valueOf(getStringValue(json, "watchStatus"));
        this.watchedEpisodes = getIntValue(json, "watchedEpisodes");
        this.totalEpisodes = getIntValue(json, "totalEpisodes");
        this.rating = getDoubleValue(json, "rating");
        this.imageUrl = getStringValue(json, "image");
        return this;
    }
}
