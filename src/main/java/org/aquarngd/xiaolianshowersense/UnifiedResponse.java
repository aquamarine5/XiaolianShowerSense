package org.aquarngd.xiaolianshowersense;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class UnifiedResponse {
    public static JSONObject Success(JSONObject data) {
        return new JSONObject()
                .fluentPut("status", "success")
                .fluentPut("data", data);
    }
    public static JSONObject Success(String data) {
        return new JSONObject()
                .fluentPut("status", "success")
                .fluentPut("data", data);
    }
    public static JSONObject Success(JSONArray data) {
        return new JSONObject()
                .fluentPut("status", "success")
                .fluentPut("data", data);
    }
    public static JSONObject Failed(String message) {
        return new JSONObject()
                .fluentPut("status", "err")
                .fluentPut("message", message);
    }
    public static JSONObject SuccessSignal() {
        return new JSONObject()
                .fluentPut("status", "success.Signal");
    }
    public static JSONObject FailedSignal() {
        return new JSONObject()
                .fluentPut("status", "err");
    }
}
