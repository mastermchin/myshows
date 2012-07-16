package ru.myshows.domain;


import org.json.JSONObject;
import ru.myshows.api.MyShowsApi;
import ru.myshows.util.JsonUtil;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 15:29:11
 * To change this template use File | Settings | File Templates.
 */

public class Show extends  JsonEvaluator implements IShow,JsonSerializable{


    private Integer showId;

    private String title;
    private String ruTitle;
    private String status;
    private String country;
    private String startedAt;
    private String endedAt;
    private Integer year;
    private Integer kinopoiskId;
    private Integer tvrageId;
    private Integer imdbId;
    private Integer watching;
    private Integer voted;
    private Double rating;
    private Integer runtime;
    private String genres;
    private Collection<Integer> genresIds;
    private Collection<Episode> episodes;
    private MyShowsApi.STATUS watchStatus;
    private String imageUrl;


    public Show() {
    }

    @Override
    public String getFilterString() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setWatchStatus(MyShowsApi.STATUS status){
        this.watchStatus = status;
    }


    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public String getTitle() {
        if (Locale.getDefault().getLanguage().equals("ru") && getRuTitle() != null && getRuTitle().length() > 0)
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getKinopoiskId() {
        return kinopoiskId;
    }

    public void setKinopoiskId(Integer kinopoiskId) {
        this.kinopoiskId = kinopoiskId;
    }

    public Integer getTvrageId() {
        return tvrageId;
    }

    public void setTvrageId(Integer tvrageId) {
        this.tvrageId = tvrageId;
    }

    public Integer getImdbId() {
        return imdbId;
    }

    public void setImdbId(Integer imdbId) {
        this.imdbId = imdbId;
    }

    public Integer getWatching() {
        return watching;
    }

    public void setWatching(Integer watching) {
        this.watching = watching;
    }

    public Integer getVoted() {
        return voted;
    }

    public void setVoted(Integer voted) {
        this.voted = voted;
    }

    public Double getRating() {
        return rating;
    }

    @Override
    public Double getYoursRating() {
        return 0.0;
    }

    @Override
    public MyShowsApi.STATUS getWatchStatus() {
        return MyShowsApi.STATUS.remove;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Collection<Integer> getGenresIds() {
        return genresIds;
    }

    public void setGenresIds(Collection<Integer> genresIds) {
        this.genresIds = genresIds;
    }

    public Collection<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Collection<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
            this.showId = getIntValue(json, "id");
            this.country = getStringValue(json, "country");
            this.endedAt = getStringValue(json, "ended");
            this.startedAt = getStringValue(json, "started");
            this.rating = getDoubleValue(json, "rating");
            this.runtime = getIntValue(json, "runtime");
            this.status = getStringValue(json, "status");
            this.title = getStringValue(json, "title");
            this.ruTitle = getStringValue(json, "ruTitle");
            this.tvrageId = getIntValue(json, "tvrageId");
            this.voted = getIntValue(json, "voted");
            this.watching = getIntValue(json, "watching");
            this.year = getIntValue(json, "year");
            this.genresIds = JsonUtil.listFromSimpleArray(getJsonArrayValue(json, "genres"));
            this.episodes = JsonUtil.listFromJson(getJsonValue(json, "episodes"), Episode.class);
            this.imdbId = getIntValue(json, "imdbId");
            this.kinopoiskId = getIntValue(json, "kinopoiskId");
            this.imageUrl = getStringValue(json, "image");


             return this;
    }

    @Override
    public String toString() {
        return "Show{" +
                "showId=" + showId +
                ", title='" + title + '\'' +
                ", ruTitle='" + ruTitle + '\'' +
                ", status='" + status + '\'' +
                ", country='" + country + '\'' +
                ", startedAt='" + startedAt + '\'' +
                ", endedAt='" + endedAt + '\'' +
                ", year=" + year +
                ", kinopoiskId=" + kinopoiskId +
                ", tvrageId=" + tvrageId +
                ", imdbId=" + imdbId +
                ", watching=" + watching +
                ", voted=" + voted +
                ", rating=" + rating +
                ", runtime=" + runtime +
                ", genresIds=" + genresIds +
                ", episodes=" + episodes +
                '}';
    }
}
