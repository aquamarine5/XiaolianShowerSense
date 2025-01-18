package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.UnifiedResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WasherHelper {
    final
    JdbcTemplate jdbcTemplate;

    public WasherHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/wash")
    public JSONObject GetWash(@RequestParam("id") int id) {
        JSONObject jsonObject = new JSONObject();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT status,location,lastUsedTime,lastWashTime,displayNo FROM showers WHERE residenceId = ?", id);
        SqlRowSet dataResult = jdbcTemplate.queryForRowSet("SELECT avgWashTime,avgWashCount,requestTimes FROM config");
        JSONObject devices = new JSONObject();
        int length = 0;
        while (rs.next()) {
            JSONObject device = new JSONObject();
            device.put("status", rs.getInt("status"));
            device.put("name", rs.getString("location"));
            device.put("wtime", rs.getTimestamp("lastWashTime").getTime());
            device.put("time", rs.getTimestamp("lastUsedTime").getTime());
            devices.put(String.valueOf(rs.getInt("displayNo")), device);
            length++;
        }
        jsonObject.put("devices", devices);
        jsonObject.put("length", length);
        if (dataResult.next()) {
            jdbcTemplate.execute("UPDATE config SET requestTimes = requestTimes + 1 LIMIT 1");
            jsonObject.put("avgWashTime", dataResult.getLong("avgWashTime"));
            jsonObject.put("avgWashCount", dataResult.getLong("avgWashCount"));
            jsonObject.put("requestTimes", dataResult.getInt("requestTimes"));
        }
        return UnifiedResponse.Success(jsonObject);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/refresh")
    public JSONObject GetWashData(@RequestParam("id") int id) {
        JSONObject jsonObject = new JSONObject();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT status,lastUsedTime,lastWashTime,displayNo FROM showers WHERE residenceId = ?", id);
        SqlRowSet dataResult = jdbcTemplate.queryForRowSet("SELECT avgWashCount,avgWashTime,requestTimes FROM config");
        JSONObject devices = new JSONObject();
        int length = 0;
        while (rs.next()) {
            JSONObject device = new JSONObject();
            device.put("status", rs.getInt("status"));
            device.put("wtime", rs.getTimestamp("lastWashTime").getTime());
            device.put("time", rs.getTimestamp("lastUsedTime").getTime());
            devices.put(String.valueOf(rs.getInt("displayNo")), device);
            length++;
        }
        jsonObject.put("devices", devices);
        jsonObject.put("length", length);
        if (dataResult.next()) {
            jsonObject.put("avgWashTime", dataResult.getLong("avgWashTime"));
            jsonObject.put("avgWashCount", dataResult.getLong("avgWashCount"));
            jsonObject.put("requestTimes", dataResult.getInt("requestTimes"));
        }
        return UnifiedResponse.Success(jsonObject);
    }
}
