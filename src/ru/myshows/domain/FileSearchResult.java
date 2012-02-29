package ru.myshows.domain;

import org.json.JSONException;
import org.json.JSONObject;
import ru.myshows.util.JsonUtil;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 16:23:21
 * To change this template use File | Settings | File Templates.
 */
public class FileSearchResult extends JsonEvaluator implements JsonSerializable {

    private Integer match;
    private String filename;
    private Integer filesize;
    private Show show;

    public Integer getMatch() {
        return match;
    }

    public void setMatch(Integer match) {
        this.match = match;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
        this.filename = getStringValue(json, "filename");
        this.filesize = getIntValue(json, "filesize");
        this.match = getIntValue(json, "match");
        this.show = (Show) JsonUtil.listFromJson(getJsonValue(json, "show"), Show.class).get(0);
        return this;
    }
}
