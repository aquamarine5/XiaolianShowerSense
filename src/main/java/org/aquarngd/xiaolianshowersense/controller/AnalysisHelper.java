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
        SqlRowSet analysisTimeResult = jdbcTemplate.queryForRowSet("SELECT mapdata,analyseStartTime,analyseEndTime FROM residenceindex WHERE residenceId = ?", residenceId);
        String startTime=null;
        String endTime=null;
        if(analysisTimeResult.next()){
            if(analysisTimeResult.getString("mapdata")==null)
                return UnifiedResponse.Failed("No such residence");
            startTime=analysisTimeResult.getString("analyseStartTime");
            endTime=analysisTimeResult.getString("analyseEndTime");
        }
        else return UnifiedResponse.Failed("No such residence");
        while (rs.next()) {
            String timePos=rs.getString("timePos");
            if(!CheckInScope(startTime,endTime,timePos)){
                jsonObject.put(timePos,JSONObject.of(
                        "maxv",rs.getInt("maxv"),
                        "minv",rs.getInt("minv"),
                        "avgv",rs.getInt("avgv")
                ));
            }
        }
        return jsonObject;
    }

    private boolean CheckInScope(String startTime,String endTime,String currentTime){
        int sh= Integer.parseInt(startTime.substring(1,2));
        int sm=Integer.parseInt(startTime.substring(3,4));
        int eh=Integer.parseInt(endTime.substring(1,2));
        int em=Integer.parseInt(endTime.substring(3,4));
        int ch=Integer.parseInt(currentTime.substring(1,2));
        int cm=Integer.parseInt(currentTime.substring(3,4));
        return sh<=ch&&ch<=eh&&sm<=cm&&cm<=em;
    }

    @GetMapping("/force_analyse")
    public JSONObject ForceAnalyse() {
        residenceController.shouldUpdateAnalysis=true;
        return new JSONObject();
    }
}
