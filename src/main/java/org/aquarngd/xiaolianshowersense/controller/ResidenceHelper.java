package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.ResidenceController;
import org.aquarngd.xiaolianshowersense.UnifiedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ResidenceHelper {
    final
    ResidenceController residenceController;

    final
    JdbcTemplate jdbcTemplate;

    public ResidenceHelper(ResidenceController residenceController, JdbcTemplate jdbcTemplate) {
        this.residenceController = residenceController;
        this.jdbcTemplate = jdbcTemplate;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/list")
    public JSONObject GetResidenceList() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT residenceId,floorId,buildingId,name FROM residenceIndex");
        JSONArray result = new JSONArray();
        while (sqlRowSet.next()) {
            result.add(new JSONObject(Map.ofEntries(
                    Map.entry("residenceId", sqlRowSet.getInt("residenceId")),
                    Map.entry("floorId", sqlRowSet.getInt("floorId")),
                    Map.entry("buildingId", sqlRowSet.getInt("buildingId")),
                    Map.entry("name", sqlRowSet.getString("name"))
            )));
        }
        return UnifiedResponse.Success(result);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/force_update")
    public JSONObject ForceUpdateAllResidence() {
        residenceController.updateAllResidences();
        return UnifiedResponse.SuccessSignal();
    }
}
