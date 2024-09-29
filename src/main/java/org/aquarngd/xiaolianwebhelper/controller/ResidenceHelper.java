package org.aquarngd.xiaolianwebhelper.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianwebhelper.data.ResidenceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ResidenceHelper {
    @Autowired
    ResidenceController residenceController;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @CrossOrigin(origins = "*")
    @RequestMapping("/list")
    public String GetResidenceList(){
        SqlRowSet sqlRowSet= jdbcTemplate.queryForRowSet("SELECT * FROM `residenceIndex`");
        JSONArray result=new JSONArray();
        while (sqlRowSet.next()){
            result.add(new JSONObject(Map.ofEntries(
                    Map.entry("residenceId",sqlRowSet.getInt("residenceId")),
                    Map.entry("floorId",sqlRowSet.getInt("floorId")),
                    Map.entry("buildingId",sqlRowSet.getInt("buildingId")),
                    Map.entry("name",sqlRowSet.getString("name"))
            )));
        }
        return result.toJSONString();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/force_update")
    public String ForceUpdateAllResidence(){
        residenceController.updateAllResidences();
        return "";
    }
}
