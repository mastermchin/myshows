package ru.myshows.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 15:54:29
 * To change this template use File | Settings | File Templates.
 */
public class Genre extends JsonEvaluator implements JsonSerializable {


    private Integer genreId;

    private String title;

    private String ruTitle;


    public Genre() {
    }

    public Genre(JSONObject json) {
        try {
            this.genreId = Integer.parseInt(json.getString("id"));
            this.title = json.getString("title");
            this.ruTitle = json.getString("ruTitle");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
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

    @Override
    public Object formJSONObject(JSONObject json) {
        this.genreId = getIntValue(json, "id", "genreId");
        this.title = getStringValue(json, "title");
        this.ruTitle = getStringValue(json, "ruTitle");
        return this;

    }
}
