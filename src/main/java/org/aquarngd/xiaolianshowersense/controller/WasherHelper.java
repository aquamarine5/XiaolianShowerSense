package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.UnifiedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;

@RestController
public class WasherHelper {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @CrossOrigin(origins = "*")
    @GetMapping("/wash")
    public String GetWash(@RequestParam("id") int id) {
        JSONObject jsonObject = new JSONObject();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(String.format("SELECT * FROM `%d`",id));
        SqlRowSet dataResult = jdbcTemplate.queryForRowSet("SELECT * FROM `data`");
        JSONArray devicesList = jsonObject.putArray("devices");
        while (rs.next()) {
            JSONObject device = new JSONObject();
            device.put("id", rs.getInt("displayNo"));
            device.put("status", rs.getInt("status"));
            device.put("name", rs.getString("location"));
            device.put("wtime",rs.getTimestamp("lastWashTime").getTime());
            device.put("time", rs.getTimestamp("lastUsedTime").getTime());
            devicesList.add(device);
        }
        if(dataResult.next()){
            jdbcTemplate.execute("UPDATE `data` SET requestTimes = requestTimes + 1");
            jsonObject.put("avgWashTime", dataResult.getLong("avgWashTime"));
            jsonObject.put("avgWashCount", dataResult.getLong("avgWashCount"));
            jsonObject.put("requestTimes", dataResult.getInt("requestTimes"));
        }
        return jsonObject.toJSONString();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/refresh")
    public String GetWashData(@RequestParam("id") int id) {
        JSONObject jsonObject = new JSONObject();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(String.format("SELECT * FROM `%d`",id));
        SqlRowSet dataResult = jdbcTemplate.queryForRowSet("SELECT * FROM `data`");
        JSONArray devicesList = jsonObject.putArray("devices");
        while (rs.next()) {
            JSONObject device = new JSONObject();
            device.put("id", rs.getInt("displayNo"));
            device.put("status", rs.getInt("status"));
            device.put("time", rs.getTimestamp("lastUsedTime").getTime());
            device.put("wtime",rs.getTimestamp("lastWashTime").getTime());
            devicesList.add(device);
        }
        if(dataResult.next()){
            jsonObject.put("avgWashTime", dataResult.getLong("avgWashTime"));
            jsonObject.put("avgWashCount", dataResult.getLong("avgWashCount"));
            jsonObject.put("requestTimes", dataResult.getInt("requestTimes"));
        }
        return UnifiedResponse.Success(jsonObject).toJSONString();
    }
}