package ru.myshows.domain;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 16:07:17
 * To change this template use File | Settings | File Templates.
 */
public interface JsonSerializable extends Serializable {

    Object formJSONObject(JSONObject json);

}
