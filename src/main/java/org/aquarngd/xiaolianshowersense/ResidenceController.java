package org.aquarngd.xiaolianshowersense;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.data.WasherStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ComponentScan("org.aquarngd.xiaolianshowersense")
@Component
public class ResidenceController {

    Logger logger;

    final
    JdbcTemplate jdbcTemplate;

    final
    XiaolianWebPortal webPortal;

    private final Integer[] supportedResidenceBuildingsId = new Integer[]{759935};

    public boolean shouldUpdateAnalysis = false;
    private final XiaolianAnalysis xiaolianAnalysis;

    public ResidenceController(JdbcTemplate jdbcTemplate, XiaolianWebPortal webPortal, XiaolianAnalysis xiaolianAnalysis) {
        logger = LoggerFactory.getLogger(ResidenceController.class);
        //webPortal = application.webPortal;
        this.jdbcTemplate = jdbcTemplate;
        this.webPortal = webPortal;
        this.xiaolianAnalysis = xiaolianAnalysis;
    }

    JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private JSONObject buildIndexResidenceRequest(int buildingId) {
        return new JSONObject(Map.ofEntries(
                Map.entry("_mp", "1_1_2"),
                Map.entry("buildingId", buildingId),
                Map.entry("campusId", 347),
                Map.entry("miniSource", 3),
                Map.entry("schoolId", 219),
                Map.entry("system", 2),
                Map.entry("netType", 1)
        ));
    }

    private JSONObject buildUpdateWasherRequest(int buildingId, int residenceId, int floorId) {
        return new JSONObject(Map.ofEntries(
                Map.entry("residenceId", residenceId),
                Map.entry("floorId", floorId),
                Map.entry("deviceType", 1),
                Map.entry("locationType", 2),
                Map.entry("buildingId", buildingId),
                Map.entry("page", 1),
                Map.entry("size", 1000),
                Map.entry("_mp", "1_1_2"),
                Map.entry("miniSource", 3),
                Map.entry("system", 2)
        ));
    }

    private void createResidenceIndexDatabase() {
        getJdbcTemplate().execute("""
                CREATE TABLE IF NOT EXISTS xiaolian.residenceIndex (
                residenceId INT PRIMARY KEY,
                floorId INT NOT NULL,
                buildingId INT NOT NULL,
                name TEXT NOT NULL
                ) CHARACTER SET utf8mb4""");
        for (int buildingId : supportedResidenceBuildingsId) {
            JSONObject residenceInfo = webPortal.sendPostRequest("https://netapi.xiaolianhb.com/m/choose/stu/residence/bathroom/byBuilding",
                    buildIndexResidenceRequest(buildingId),
                    "https://netapi.xiaolianhb.com/2020042916153901/4.9.2.0/m/choose/stu/residence/bathroom/byBuilding");
            logger.info("get residences data: {}", residenceInfo.toJSONString());
            residenceInfo = residenceInfo.getJSONObject("data").getJSONArray("residences").getJSONObject(0);
            getJdbcTemplate().execute(String.format("INSERT INTO `residenceIndex` (residenceId, floorId, buildingId, name) VALUES (%d, %d, %d, '%s')",
                    residenceInfo.getInteger("id"),
                    residenceInfo.getInteger("parentId"),
                    buildingId,
                    residenceInfo.getString("fullName")));
        }
    }

    private void checkResidenceDatabase(int residenceId) {
        getJdbcTemplate().execute(String.format("""
                CREATE TABLE IF NOT EXISTS xiaolian.%d (
                deviceId INT PRIMARY KEY,
                location VARCHAR(50) NOT NULL,
                displayNo INT NOT NULL,
                status INT NOT NULL,
                lastUsedTime TIMESTAMP NOT NULL,
                lastWashTime TIMESTAMP NOT NULL
                ) CHARACTER SET utf8mb4""", residenceId));
        logger.warn("Created {} database", residenceId);
    }

    private void updateShower(JSONObject deviceObject, int residenceId) {
        WasherStatus deviceStatus = WasherStatus.valueOf(deviceObject.getInteger("deviceStatus"));
        int deviceId = deviceObject.getInteger("deviceId");
        SqlRowSet rs = getJdbcTemplate().queryForRowSet(String.format("SELECT * FROM `%d` WHERE deviceId = %d", residenceId, deviceId));
        if (rs.next()) {
            if (WasherStatus.valueOf(rs.getInt("status")) == WasherStatus.NOT_USING &&
                    deviceStatus == WasherStatus.USING) {
                xiaolianAnalysis.residencesLastUsedTime.get(residenceId).put(deviceId, System.currentTimeMillis());
                getJdbcTemplate().execute(String.format("UPDATE `%d` SET lastUsedTime = NOW() WHERE deviceId = %d", residenceId, deviceId));
                logger.info("sql: run sql update lastUsedTime at residenceId:{}, deviceId:{}", residenceId, deviceId);
            }
            if (WasherStatus.valueOf(rs.getInt("status")) == WasherStatus.USING &&
                    deviceStatus == WasherStatus.NOT_USING) {
                long time = System.currentTimeMillis() - rs.getTimestamp("lastUsedTime").getTime();
                if (time <= 3600000L) {
                    SqlRowSet rrs = getJdbcTemplate().queryForRowSet("SELECT * FROM `data`");
                    rrs.next();
                    int count = rrs.getInt("avgWashCount");
                    long newAvgTime = (rrs.getLong("avgWashTime") * count + time) / (count + 1);
                    xiaolianAnalysis.averageWashTime = newAvgTime;
                    getJdbcTemplate().execute("UPDATE `data` SET avgWashTime = " + newAvgTime);
                    getJdbcTemplate().execute("UPDATE `data` SET avgWashCount = avgWashCount + 1");
                    getJdbcTemplate().execute(String.format("UPDATE `%d` SET lastWashTime = NOW() WHERE deviceId = %d", residenceId, deviceId));
                    xiaolianAnalysis.residencesLastWashTime.get(residenceId).put(deviceId, System.currentTimeMillis());
                    logger.info("sql: update avgWashTime");
                } else {
                    logger.warn("sql: pass time too large: {}", time);
                }
            }
            getJdbcTemplate().execute(String.format("UPDATE `%d` SET status = %d WHERE deviceId = %d", residenceId, deviceObject.getInteger("deviceStatus"), deviceId));
        } else {
            getJdbcTemplate().execute(String.format("INSERT INTO `%d` (deviceId, location, status, lastUsedTime,lastWashTime, displayNo) VALUES (%d, '%s', %d, NOW(),NOW(), %d)",
                    residenceId,
                    deviceId,
                    deviceObject.getString("location"),
                    deviceStatus.value(),
                    deviceObject.getInteger("dispNo")));
            logger.info("sql: run sql insert into.");
        }
    }

    private void updateResidence(JSONObject postBody, int residenceId) {
        if (!isDatabaseExisted(String.valueOf(residenceId))) checkResidenceDatabase(residenceId);
        JSONArray deviceList = webPortal.sendPostRequest("https://netapi.xiaolianhb.com/m/net/stu/residence/listDevice",
                        postBody,
                        "https://netapi.xiaolianhb.com/2020042916153901/4.9.2.0/m/net/stu/residence/listDevice")
                .getJSONObject("data").getJSONArray("deviceInListInfo");
        if(!xiaolianAnalysis.residencesLastWashTime.containsKey(residenceId))
            xiaolianAnalysis.residencesLastWashTime.put(residenceId,new HashMap<>());
        if(!xiaolianAnalysis.residencesLastUsedTime.containsKey(residenceId))
            xiaolianAnalysis.residencesLastUsedTime.put(residenceId,new HashMap<>());
        if(shouldUpdateAnalysis){
            xiaolianAnalysis.UpdateAnalysis(deviceList,residenceId);
        }
        logger.info("post http.");
        for (int i = 0; i < deviceList.size(); i++) {
            updateShower(deviceList.getJSONObject(i), residenceId);
        }
    }

    public void updateAllResidences() {
        if (!isDatabaseExisted("residenceIndex")) createResidenceIndexDatabase();
        logger.info("Update All Residences.");
        SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM `residenceIndex`");
        while (sqlRowSet.next()) {
            updateResidence(buildUpdateWasherRequest(
                    sqlRowSet.getInt("buildingId"), sqlRowSet.getInt("residenceId"), sqlRowSet.getInt("floorId")), sqlRowSet.getInt("residenceId"));
        }
        if(shouldUpdateAnalysis) shouldUpdateAnalysis=false;
    }

    private boolean isDatabaseExisted(String id) {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = Objects.requireNonNull(getJdbcTemplate().getDataSource()).getConnection();
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
