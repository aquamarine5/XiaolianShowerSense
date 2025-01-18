package org.aquarngd.xiaolianshowersense.controller;

import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.ResidenceController;
import org.aquarngd.xiaolianshowersense.UnifiedResponse;
import org.aquarngd.xiaolianshowersense.XiaolianAnalysis;
import org.aquarngd.xiaolianshowersense.XiaolianShowerSenseApplication;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

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
        if (!isDatabaseExisted(residenceId + "_analysis"))
            return UnifiedResponse.Success("x");
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM `" + residenceId + "_analysis`");
        JSONObject jsonObject = new JSONObject();
        SqlRowSet analysisTimeResult = jdbcTemplate.queryForRowSet("SELECT mapdata,analyseStartTime,analyseEndTime FROM residenceIndex WHERE residenceId = ?", residenceId);
        int startTime = -1;
        int endTime = -1;
        if (analysisTimeResult.next()) {
            if (analysisTimeResult.getString("mapdata") == null)
                return UnifiedResponse.Failed("No such residence");
            startTime = analysisTimeResult.getInt("analyseStartTime");
            endTime = analysisTimeResult.getInt("analyseEndTime");
        } else return UnifiedResponse.Failed("No such residence");
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

    private boolean isDatabaseExisted(String id) {
        Connection connection = null;
        ResultSet rs = null;
        var logger = LoggerFactory.getLogger(AnalysisHelper.class);
        try {
            connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
            DatabaseMetaData data = connection.getMetaData();
            String[] types = {"TABLE"};
            rs = data.getTables(null, null, id, types);
            if (rs.next()) return true;
        } catch (SQLException e) {
            logger.error("error sql: {}", e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                logger.error("error when closed sql: {}", e.getMessage());
            }
        }
        return false;
    }
}
