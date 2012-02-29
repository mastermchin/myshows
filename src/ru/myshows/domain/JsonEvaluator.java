package ru.myshows.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 01.06.2011
 * Time: 15:38:10
 * To change this template use File | Settings | File Templates.
 */
public class JsonEvaluator {

     public String getStringValue(JSONObject json, String... fields) {
        for (String field : fields) {
            if (json.has(field))
                try {
                    return json.getString(field);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

    public Integer getIntValue(JSONObject json, String... fields) {
        for (String field : fields) {
            if (json.has(field))
                try {
                    return json.getInt(field);
                } catch (JSONException e) {
                   //e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

    public Double getDoubleValue(JSONObject json, String... fields) {
        for (String field : fields) {
            if (json.has(field))
                try {
                    return json.getDouble(field);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

    public JSONObject getJsonValue(JSONObject json, String... fields) {
        for (String field : fields) {
            if (json.has(field))
                try {
                    return json.getJSONObject(field);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

    public JSONArray getJsonArrayValue(JSONObject json, String... fields) {
        for (String field : fields) {
            if (json.has(field))
                try {
                    return json.getJSONArray(field);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

}
