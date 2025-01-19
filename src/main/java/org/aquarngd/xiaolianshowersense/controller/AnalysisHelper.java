package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.ResidenceController;
import org.aquarngd.xiaolianshowersense.UnifiedResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @CrossOrigin(origins = "*")
    @GetMapping("/analysis")
    public JSONObject GetAnalysis(@RequestParam int residenceId) {
        SqlRowSet analysisTimeResult = jdbcTemplate.queryForRowSet("SELECT mapdata,analyseStartTime,analyseEndTime FROM residenceIndex WHERE residenceId = ?", residenceId);
        int startTime, endTime;
        if (analysisTimeResult.next()) {
            if (analysisTimeResult.getString("mapdata") == null)
                return UnifiedResponse.Success("x");
            startTime = analysisTimeResult.getInt("analyseStartTime");
            endTime = analysisTimeResult.getInt("analyseEndTime");
        } else
            return UnifiedResponse.Failed("No such residence");
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT minv,maxv,avgv,timePos FROM analyses WHERE residenceId = ?", residenceId);
        Boolean configRs=jdbcTemplate.queryForObject("SELECT isAnalysisEnabled FROM config LIMIT 1",Boolean.class);
        JSONObject jsonObject = new JSONObject();
        while (rs.next()) {
            int timePos = rs.getInt("timePos");
            if (startTime <= timePos && timePos <= endTime) {
                jsonObject.put(String.valueOf(timePos), JSONObject.of(
                        "maxv", rs.getInt("maxv"),
                        "minv", rs.getInt("minv"),
                        "avgv", rs.getInt("avgv")
                ));
            }
        }
        jsonObject.put("isEnabled",configRs);
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        return UnifiedResponse.Success(jsonObject);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/force_analyse")
    public JSONObject ForceAnalyse() {
        residenceController.shouldUpdateAnalysis = true;
        return UnifiedResponse.SuccessSignal();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/analyser_enabled")
    public JSONObject AnalyserEnabled(){
        jdbcTemplate.execute("UPDATE config SET isAnalysisEnabled=true LIMIT 1");
        return UnifiedResponse.SuccessSignal();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/analyser_disabled")
    public JSONObject AnalyserDisabled(){
        jdbcTemplate.execute("UPDATE config SET isAnalysisEnabled=false LIMIT 1");
        return UnifiedResponse.SuccessSignal();
    }
}
