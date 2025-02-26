package com.infinity.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtil {
    private static Gson gson = new GsonBuilder().create();
    private static JsonParser jsonp = new JsonParser();

    public static <T> T parseJson(String json, Class<T> t) {
        return gson.fromJson(json, t);
    }

    public static JsonObject parseJson(String json) {
        return jsonp.parse(json).getAsJsonObject();
    }

    public static String parseObject(Object obj) {
        return gson.toJson(obj);
    }

    public static Map<String, Object> toMap(String jsonString) {
        // 定义Map的类型
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

        // 将JSON字符串转换为Map
        return gson.fromJson(jsonString, mapType);
    }
}
