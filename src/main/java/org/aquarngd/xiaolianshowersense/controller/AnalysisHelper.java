package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.ResidenceController;
import org.aquarngd.xiaolianshowersense.UnifiedResponse;
import org.aquarngd.xiaolianshowersense.XiaolianAnalysis;
import org.aquarngd.xiaolianshowersense.XiaolianShowerSenseApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisHelper {
    private final ResidenceController residenceController;
    private final JdbcTemplate jdbcTemplate;

    public AnalysisHelper(ResidenceController residenceController, JdbcTemplate jdbcTemplate) {
        this.residenceController = residenceController;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/analysis")
    public JSONObject GetAnalysis(@RequestParam int residenceId) {
        SqlRowSet rs=jdbcTemplate.queryForRowSet("SELECT * FROM `"+residenceId+"_analysis`");
        JSONObject jsonObject = new JSONObject();
        SqlRowSet analysisTimeResult = jdbcTemplate.queryForRowSet("SELECT mapdata,analyseStartTime,analyseEndTime FROM residenceIndex WHERE residenceId = ?", residenceId);
        int startTime=-1;
        int endTime=-1;
        if(analysisTimeResult.next()){
            if(analysisTimeResult.getString("mapdata")==null)
                return UnifiedResponse.Failed("No such residence");
            startTime=analysisTimeResult.getInt("analyseStartTime");
            endTime=analysisTimeResult.getInt("analyseEndTime");
        }
        else return UnifiedResponse.Failed("No such residence");
        while (rs.next()) {
            int timePos=rs.getInt("timePos");
            if(startTime<=timePos&&timePos<=endTime){
                jsonObject.put(String.valueOf(timePos),JSONObject.of(
                        "maxv",rs.getInt("maxv"),
                        "minv",rs.getInt("minv"),
                        "avgv",rs.getInt("avgv")
                ));
            }
        }
        jsonObject.put("startTime",startTime);
        jsonObject.put("endTime",endTime);
        return jsonObject;
    }

    @GetMapping("/force_analyse")
    public JSONObject ForceAnalyse() {
        residenceController.shouldUpdateAnalysis=true;
        return new JSONObject();
    }
}
