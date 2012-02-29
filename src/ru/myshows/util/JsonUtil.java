package ru.myshows.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.myshows.domain.JsonSerializable;
import ru.myshows.domain.UserShow;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.05.2011
 * Time: 18:56:22
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtil {

    public static Object objectFromJson(JSONObject json, Class clazz) {
        if (json == null) return null;
        JsonSerializable o = null;
        try {
            o = (JsonSerializable) clazz.newInstance();
            o.formJSONObject(json);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;

    }

    public static List listFromJson(JSONObject json, Class targetClass) {
        if (json == null) return null;
        List list = new ArrayList();
        Iterator i = json.keys();
        try {
            while (i.hasNext()) {
                String key = i.next().toString();
                JSONObject j = null;
                try {
                    j = json.getJSONObject(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
                JsonSerializable o = (JsonSerializable) targetClass.newInstance();
                list.add(o.formJSONObject(j));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List listFromJsonArray(JSONArray array, Class targetClass) {
        if (array == null) return null;
        List list = new ArrayList();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject j = array.getJSONObject(i);
                JsonSerializable o = (JsonSerializable) targetClass.newInstance();
                list.add(o.formJSONObject(j));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List listFromSimpleArray(JSONArray array) {
        if (array == null) return null;
        List list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            try {
                list.add(array.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static Map mapFromJson(JSONObject json, Class targetClass) {
        if (json == null) return null;
        Map map = new HashMap();
        Iterator iter = json.keys();
        try {
            while (iter.hasNext()) {
                List news = new ArrayList();
                String key = iter.next().toString();
                JSONArray array = json.getJSONArray(key);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject j = array.getJSONObject(i);
                    JsonSerializable o = (JsonSerializable) targetClass.newInstance();
                    news.add(o.formJSONObject(j));
                }
                map.put(key, news);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map mapFromJson2(JSONObject json, Class targetClass) {
        if (json == null) return null;
        Map map = new HashMap();
        Iterator iter = json.keys();
        try {
            while (iter.hasNext()) {

                String key = iter.next().toString();
                JSONObject genre = json.getJSONObject(key);
                JsonSerializable o = (JsonSerializable) targetClass.newInstance();
                map.put(Integer.parseInt(key), o.formJSONObject(genre));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }


}
