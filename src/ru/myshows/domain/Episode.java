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
 * Time: 15:43:02
 * To change this template use File | Settings | File Templates.
 */

public class Episode extends JsonEvaluator implements JsonSerializable, Filterable {

    private static final DateFormat DF = new SimpleDateFormat("dd.MM.yyyy");


    private Integer episodeId;
    private String title;
    private String ruTitle;
    private Integer showId;
    private int seasonNumber;
    private Date airDate;
    private String productionNumber;
    private int sequenceNumber;
    private int episodeNumber;
    private String tvrageLink;
    private String imageUrl;
    private String shortName;
    private boolean isChecked;
    private UserShow show;


    @Override
    public String getFilterString() {
        return title;
    }

    public UserShow getShow() {
        return show;
    }

    public void setShow(UserShow show) {
        this.show = show;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Episode(Integer episodeId, String title, int seasonNumber, String shortName) {
        this.episodeId = episodeId;
        this.title = title;
        this.seasonNumber = seasonNumber;
        this.shortName = shortName;
    }

    public Episode() {
    }


    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public String getTitle() {
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

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Date getAirDate() {
        return airDate;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public String getProductionNumber() {
        return productionNumber;
    }

    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getTvrageLink() {
        return tvrageLink;
    }

    public void setTvrageLink(String tvrageLink) {
        this.tvrageLink = tvrageLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
        try {
            this.episodeId = getIntValue(json, "episodeId", "id");
            this.seasonNumber = getIntValue(json, "seasonNumber");
            this.showId = getIntValue(json, "showId");
            this.title = getStringValue(json, "title");
            this.shortName = getStringValue(json, "shortName");
            this.tvrageLink = getStringValue(json, "tvrageLink");
            this.imageUrl = getStringValue(json, "image");
           // this.sequenceNumber = getIntValue(json, "sequenceNumber");
            this.episodeNumber = getIntValue(json, "episodeNumber");
            this.productionNumber = getStringValue(json, "productionNumber");
            this.airDate = DF.parse(getStringValue(json, "airDate"));
        } catch (ParseException e) {}
        return this;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "episodeId=" + episodeId +
                ", title='" + title + '\'' +
                '}';
    }
}
