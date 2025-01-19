package org.aquarngd.xiaolianshowersense;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

@ComponentScan("org.aquarngd.xiaolianshowersense")
@Component
public class XiaolianWebPortal {

    Logger logger;
    String accessToken = "";
    String refreshToken = "";

    final
    JdbcTemplate jdbcTemplate;

    public RestTemplate restTemplate = new RestTemplate();

    public XiaolianWebPortal(JdbcTemplate jdbcTemplate) {
        logger = LoggerFactory.getLogger(XiaolianWebPortal.class);
        this.jdbcTemplate = jdbcTemplate;
    }

    public HttpHeaders getHttpHeaders(@Nullable String referer) {
        if (Objects.equals(accessToken, "") || Objects.equals(refreshToken,"")) getTokens();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("accessToken", accessToken);
        httpHeaders.set("refreshToken", refreshToken);
        if (referer != null)
            httpHeaders.set(HttpHeaders.REFERER, referer);
        return httpHeaders;
    }

    private static JSONObject getLoginJsonObject() {
        JSONObject loginData = new JSONObject();
        loginData.put("appList", "[]");
        loginData.put("mobile", "19030827318");
        loginData.put("password", "070304syz");
        loginData.put("appVersion", "1.4.8");
        loginData.put("system", "2");
        loginData.put("model", "DCO-AL00");
        loginData.put("lon", "");
        loginData.put("appSource", "1");
        loginData.put("systemVersion", "12");
        loginData.put("brand", "HUAWEI");
        loginData.put("uniqueId", "8c185942d3b9cf82");
        loginData.put("lat", "");
        return loginData;
    }

    public static JSONObject buildIndexResidenceRequest(int buildingId) {
        return new JSONObject(Map.ofEntries(
                Map.entry("_mp", "1_1_2"),
                Map.entry("buildingId", buildingId),
                Map.entry("campusId", 347),
                Map.entry("miniSource", 3),
                Map.entry("schoolId", 219),
                Map.entry("system", 2),
                Map.entry("netType", 1)
        ));
    }

    public static JSONObject buildUpdateWasherRequest(int buildingId, int residenceId, int floorId) {
        return new JSONObject(Map.ofEntries(
                Map.entry("residenceId", residenceId),
                Map.entry("floorId", floorId),
                Map.entry("deviceType", 1),
                Map.entry("locationType", 2),
                Map.entry("buildingId", buildingId),
                Map.entry("page", 1),
                Map.entry("size", 1000),
                Map.entry("_mp", "1_1_2"),
                Map.entry("miniSource", 3),
                Map.entry("system", 2)
        ));
    }

    private void getTokens() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT accessToken,refreshToken FROM config LIMIT 1");
        if (rs.next()) {
            accessToken = rs.getString("accessToken");
            refreshToken = rs.getString("refreshToken");
        }
    }

    public void relogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(getLoginJsonObject(), headers);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(
                "https://mapi.xiaolianhb.com/mp/login", httpEntity, JSONObject.class);
        logger.info("try re-login.");
        JSONObject response = responseEntity.getBody();
        if (response != null) {
            logger.info("sql: login response: {}", response.toJSONString());
            String newAccessToken = response.getJSONObject("data").getString("accessToken");
            String newRefreshToken = response.getJSONObject("data").getString("refreshToken");
            jdbcTemplate.update("UPDATE config SET accessToken = ?, refreshToken = ? LIMIT 1",
                    newAccessToken, newRefreshToken);
            accessToken = newAccessToken;
            refreshToken = newRefreshToken;
        }
    }

    public JSONObject sendPostRequest(String url, JSONObject data, @Nullable String referer) {
        return trySendPostRequest(url, data, referer);
    }

    private JSONObject trySendPostRequest(String url, JSONObject data, @Nullable String referer) {
        HttpEntity<JSONObject> httpPostEntity = new HttpEntity<>(data, getHttpHeaders(referer));
        ResponseEntity<JSONObject> postResponse = null;
        try {
            postResponse = restTemplate.postForEntity(url, httpPostEntity, JSONObject.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                relogin();
                postResponse = restTemplate.postForEntity(url, new HttpEntity<>(data, getHttpHeaders(referer)), JSONObject.class);
            }
            logger.info("error 401: {}", e.getResponseBodyAsString());
        }
        return postResponse.getBody();
    }
}
