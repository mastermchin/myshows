package ru.myshows.domain;

import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 16:24:31
 * To change this template use File | Settings | File Templates.
 */
public class ProfileStats extends JsonEvaluator implements JsonSerializable{

    private Double watchedHours;
    private Double remainingHours;
    private Integer watchedEpisodes;
    private Integer remainingEpisodes;
    private Integer watchedDays;
    private Integer remainingDays;


    public Double getWatchedHours() {
        return watchedHours;
    }

    public void setWatchedHours(Double watchedHours) {
        this.watchedHours = watchedHours;
    }

    public Double getRemainingHours() {
        return remainingHours;
    }

    public void setRemainingHours(Double remainingHours) {
        this.remainingHours = remainingHours;
    }

    public Integer getWatchedEpisodes() {
        return watchedEpisodes;
    }

    public void setWatchedEpisodes(Integer watchedEpisodes) {
        this.watchedEpisodes = watchedEpisodes;
    }

    public Integer getRemainingEpisodes() {
        return remainingEpisodes;
    }

    public void setRemainingEpisodes(Integer remainingEpisodes) {
        this.remainingEpisodes = remainingEpisodes;
    }

    public Integer getWatchedDays() {
        return watchedDays;
    }

    public void setWatchedDays(Integer watchedDays) {
        this.watchedDays = watchedDays;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
        this.watchedHours = getDoubleValue(json, "watchedHours");
        this.remainingHours = getDoubleValue(json, "remainingHours");
        this.watchedEpisodes = getIntValue(json, "watchedEpisodes");
        this.remainingEpisodes = getIntValue(json, "remainingEpisodes");
        this.watchedDays = getIntValue(json, "watchedDays");
        this.remainingDays = getIntValue(json, "remainingDays");
        return this;
    }
}
