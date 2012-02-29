package ru.myshows.domain;

import org.json.JSONObject;
import ru.myshows.api.MyShowsApi;
import ru.myshows.util.JsonUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 15:56:28
 * To change this template use File | Settings | File Templates.
 */
public class Profile extends JsonEvaluator implements JsonSerializable{

    private String login;
    private String avatarUrl;
    private Integer wastedTime; // in hours
    private String gender;
    private List<Profile> friends;
    private List<Profile> followers;
    private ProfileStats stats;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getWastedTime() {
        return wastedTime;
    }

    public void setWastedTime(Integer wastedTime) {
        this.wastedTime = wastedTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Profile> getFriends() {
        return friends;
    }

    public void setFriends(List<Profile> friends) {
        this.friends = friends;
    }

    public List<Profile> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Profile> followers) {
        this.followers = followers;
    }

    public ProfileStats getStats() {
        return stats;
    }

    public void setStats(ProfileStats stats) {
        this.stats = stats;
    }

    @Override
    public Object formJSONObject(JSONObject json) {
        this.login = getStringValue(json, "login");
        this.avatarUrl = getStringValue(json, "avatar");
        this.wastedTime = getIntValue(json, "wastedTime");
        this.gender = getStringValue(json, "gender");
        this.friends = JsonUtil.listFromJsonArray(getJsonArrayValue(json, "friends"), Profile.class);
        this.stats = (ProfileStats) JsonUtil.objectFromJson(getJsonValue(json, "stats"), ProfileStats.class);
        return this;
    }
}
