package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.UnifiedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapHelper {
    final
    JdbcTemplate jdbcTemplate;

    public MapHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/map")
    public String GetResidenceMap(@RequestParam int id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT mapdata,contributors FROM residenceIndex where residenceId = ?", id);
        JSONObject jsonObject = new JSONObject();
        if (sqlRowSet.next()) {
            jsonObject.put("isShowMap", sqlRowSet.getString("mapdata") != null);
            jsonObject.put("mapdata", JSONArray.parse(sqlRowSet.getString("mapdata")));
            jsonObject.put("contributors", sqlRowSet.getString("contributors"));

            return UnifiedResponse.Success(jsonObject).toJSONString();
        } else return UnifiedResponse.Failed("No such residence").toJSONString();
    }
}
