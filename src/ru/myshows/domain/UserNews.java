package ru.myshows.domain;

import org.json.JSONException;
import org.json.JSONObject;
import ru.myshows.api.MyShowsApi;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 16:21:16
 * To change this template use File | Settings | File Templates.
 */
public class UserNews extends JsonEvaluator implements JsonSerializable, Filterable {

    private Integer episodeId;
    private int showId;
    private String show;
    private String title;
    private String login;
    private MyShowsApi.GENDER gender;
    private int episodes;
    private String episode;
    private String action;

    @Override
    public String getFilterString() {
        return title+show+login;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public MyShowsApi.GENDER getGender() {
        return gender;
    }

    public void setGender(MyShowsApi.GENDER gender) {
        this.gender = gender;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
        this.showId = getIntValue(json, "showId");
        this.title = getStringValue(json, "title");
        this.show = getStringValue(json, "show");
        this.login = getStringValue(json, "login");
        this.episodes = getIntValue(json, "episodes");
        this.episode = getStringValue(json, "episode");
        this.action = getStringValue(json, "action");
        this.episodeId = getIntValue(json, "episodeId");

        return this;
    }

    @Override
    public String toString() {
        return "UserNews{" +
                "episodeId=" + episodeId +
                ", showId=" + showId +
                ", show='" + show + '\'' +
                ", title='" + title + '\'' +
                ", login='" + login + '\'' +
                ", episode='" + episode + '\'' +
                ", action='" + action + '\'' +
                ", episodes=" + episodes +
                '}';
    }
}
