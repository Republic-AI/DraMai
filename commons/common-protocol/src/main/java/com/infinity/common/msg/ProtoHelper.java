package com.infinity.common.msg;

import com.google.gson.JsonObject;
import com.infinity.common.msg.common.ResponseOk;
import com.infinity.common.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProtoHelper {
    private static Logger log = LoggerFactory.getLogger(ProtoHelper.class);

    public static BaseMsg parseJSON(String json) {
        JsonObject jobj = GsonUtil.parseJson(json);
        int code = jobj.get("command").getAsInt();
        // BaseMsg msg = JSONObject.parseObject(json, BaseMsg.class);
        return parseJSON(json, code);
    }

    public static BaseMsg parseJSON(String json, int code) {
        Class<? extends BaseMsg> msgClass = MsgFactory.getMsgClass(MsgFactory.MsgType.Request, code);
        if (msgClass == null) {
            log.error("parseJSON fail, unkown parse msg, msg={}, command={}", json, code);
            return null;
        }

        return GsonUtil.parseJson(json, msgClass);
    }

    public static BaseMsg parseResponseJSON(String json) {
        JsonObject object = GsonUtil.parseJson(json);
        int code = object.get("code").getAsInt();
        if (code != 0) {
            return GsonUtil.parseJson(json, ResponseOk.class);
        }

        int command = object.get("command").getAsInt();
        return parseResponseJSON(json, command);
    }

    public static BaseMsg parseResponseJSON(String json, int code) {
        Class<? extends BaseMsg> msgClass = MsgFactory.getMsgClass(MsgFactory.MsgType.Response, code);
        if (msgClass == null) {
            log.error("parseResponseJSON fail, unkown parse msg, msg={}, command={}", json, code);
            return null;
        }

        return GsonUtil.parseJson(json, msgClass);
    }

    public static String parseObject(Object obj) {
        return GsonUtil.parseObject(obj);
    }
}
