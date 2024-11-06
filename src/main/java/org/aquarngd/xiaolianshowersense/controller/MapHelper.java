package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson.JSONArray;
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
    public String GetResidenceMap(@RequestParam int residenceId){
        SqlRowSet sqlRowSet= jdbcTemplate.queryForRowSet("SELECT mapdata FROM `residenceIndex` where residenceId = ?",residenceId);
        if(sqlRowSet.next()){
            return UnifiedResponse.Success(sqlRowSet.getString("mapdata")).toJSONString();
        }
        else return UnifiedResponse.Failed("No such residence").toJSONString();
    }
}
