package org.aquarngd.xiaolianshowersense;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Objects;

@Component
public class XiaolianWebPortal {
    
    Logger logger;
    String accessToken="";
    String refreshToken="";

    XiaolianwebhelperApplication application;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public RestTemplate restTemplate=new RestTemplate();

    public XiaolianWebPortal(){
        logger= LoggerFactory.getLogger(XiaolianWebPortal.class);
    }

    JdbcTemplate getJdbcTemplate(){
        return jdbcTemplate;
    }
    public HttpHeaders getHttpHeaders(@Nullable String referer){
        if (!isDatabaseExisted()) {
            createDatabase();
            logger.debug("sql: create data table");
            getJdbcTemplate().execute(String.format("INSERT INTO `data` (accessToken,refreshToken) VALUES ('%s', '%s')",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NzQ1ODcwIiwib3MiOiIwIiwiaXNzIjoiaHR0cHM6Ly94aWFvbGlhbi5pbyIsImlhdCI6MTcyNjkwODcyOSwiZXhwIjoxNzI2OTI2NzI5fQ.WK85D2fwxnd6SsWs8KKBKhm75nBki8C7q_Cs331iazIFo6Ji5JxW7lC2wVkBHcd4XoAnFMI9tUWpOI5rXBMVpg",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NzQ1ODcwIiwib3MiOiIwIiwiaXNzIjoiaHR0cHM6Ly94aWFvbGlhbi5pbyIsImlhdCI6MTcyNjkwODcyOSwiZXhwIjoxNzI4MjA0NzI5fQ.Bv2wkS8m7O2gHOfDjxFPRHpBgis4KbXO1R-_Kp0ly3ohPjL9hDQWev66_XjGU0DrnS59B5ZWG0MSh7aPi86SBg"));

        }
        if(Objects.equals(accessToken, "")) getTokens();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("accessToken", accessToken);
        httpHeaders.set("refreshToken", refreshToken);
        if(referer!=null)
            httpHeaders.set(HttpHeaders.REFERER,referer);
        return httpHeaders;
    }

    private void createDatabase() {
        getJdbcTemplate().execute("""
                CREATE TABLE xiaolian.data (
                accessToken TEXT NOT NULL,
                refreshToken TEXT NOT NULL,
                avgWashCount INT DEFAULT 0,
                avgWashTime BIGINT DEFAULT 0,
                requestTimes INT DEFAULT 0
                ) CHARACTER SET utf8mb4""");
    }
    private static JSONObject getLoginJsonObject() {
        JSONObject loginData = new JSONObject();
        loginData.put("appList", new JSONArray());
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
    private void getTokens() {

        SqlRowSet rs = getJdbcTemplate().queryForRowSet("SELECT * FROM `data`");
        if (rs.next()) {
            accessToken=rs.getString("accessToken");
            refreshToken=rs.getString("refreshToken");
        }
    }
    private void relogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(getLoginJsonObject(), headers);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity("https://mapi.xiaolianhb.com/mp/login", httpEntity, JSONObject.class);
        logger.info("try re-login.");
        JSONObject response = responseEntity.getBody();
        if (response != null) {
            logger.info("sql: login response: {}", response.toJSONString());
            String newAccessToken = response.getJSONObject("data").getString("accessToken");
            String newRefreshToken = response.getJSONObject("data").getString("refreshToken");
            getJdbcTemplate().execute("UPDATE `data` SET accessToken = '" + newAccessToken + "'");
            getJdbcTemplate().execute("UPDATE `data` SET refreshToken = '" + newRefreshToken + "'");
            accessToken=newAccessToken;
            refreshToken=newRefreshToken;
        }
    }
    public JSONObject sendPostRequest(String url, JSONObject data, @Nullable String referer){
        return trySendPostRequest(url, data, referer);
    }
    private JSONObject trySendPostRequest(String url,JSONObject data,@Nullable String referer){
        HttpEntity<JSONObject> httpPostEntity = new HttpEntity<>(data, getHttpHeaders(referer));
        ResponseEntity<JSONObject> postResponse=null;
        try{
             postResponse= restTemplate.postForEntity(url, httpPostEntity, JSONObject.class);
        }
        catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                relogin();
                postResponse= restTemplate.postForEntity(url, new HttpEntity<>(data, getHttpHeaders(referer)), JSONObject.class);
            }
            logger.info("error 401: {}", e.getResponseBodyAsString());
        }
        return postResponse.getBody();
    }

    private boolean isDatabaseExisted() {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = Objects.requireNonNull(getJdbcTemplate().getDataSource()).getConnection();
            DatabaseMetaData data = connection.getMetaData();
            String[] types = {"TABLE"};
            rs = data.getTables(null, null, "data", types);
            if (rs.next()) return true;
        } catch (SQLException e) {

            logger.error("error sql: {}",e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                logger.error("error when closed sql: {}",e.getMessage());
            }
        }
        return false;
    }
}
