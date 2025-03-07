package org.aquarngd.xiaolianshowersense;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.data.WasherStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashMap;

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
        this.jdbcTemplate = jdbcTemplate;
        this.webPortal = webPortal;
        this.xiaolianAnalysis = xiaolianAnalysis;
    }


    public void setupResidenceIndex(int buildingId) {
        JSONObject residenceInfo = webPortal.sendPostRequest("https://netapi.xiaolianhb.com/m/choose/stu/residence/bathroom/byBuilding",
                XiaolianWebPortal.buildIndexResidenceRequest(buildingId),
                "https://netapi.xiaolianhb.com/2020042916153901/4.9.2.0/m/choose/stu/residence/bathroom/byBuilding");
        logger.info("get residences data: {}", residenceInfo.toJSONString());
        residenceInfo = residenceInfo.getJSONObject("data").getJSONArray("residences").getJSONObject(0);
        jdbcTemplate.update("INSERT INTO `residenceIndex` (residenceId, floorId, buildingId, name) VALUES (?, ?, ?, ?)",
                residenceInfo.getInteger("id"),
                residenceInfo.getInteger("parentId"),
                buildingId,
                residenceInfo.getString("fullName"));
    }

    public void setupAllSupportedResidenceIndex() {
        for (int buildingId : supportedResidenceBuildingsId) {
            setupResidenceIndex(buildingId);
        }
    }

    private void checkResidenceIndex(int residenceId, int buildingId) {
        String sql = "SELECT residenceId FROM residenceIndex WHERE residenceId = ? LIMIT 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, residenceId);
        if (!sqlRowSet.next()) {
            setupResidenceIndex(buildingId);
        }
    }

    private void updateShower(JSONObject deviceObject, int residenceId) {
        WasherStatus deviceStatus = WasherStatus.valueOf(deviceObject.getInteger("deviceStatus"));
        int deviceId = deviceObject.getInteger("deviceId");
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT status,lastUsedTime FROM showers WHERE residenceId=? and deviceId =?", residenceId, deviceId);
        if (rs.next()) {
            if (WasherStatus.valueOf(rs.getByte("status")) == WasherStatus.NOT_USING &&
                    deviceStatus == WasherStatus.USING) {
                xiaolianAnalysis.residencesLastUsedTime.get(residenceId).put(deviceId, System.currentTimeMillis());
                jdbcTemplate.update("UPDATE showers SET lastUsedTime = NOW() WHERE residenceId=? and deviceId =?", residenceId, deviceId);
                logger.info("sql: run sql update lastUsedTime at residenceId:{}, deviceId:{}", residenceId, deviceId);
            }
            if (WasherStatus.valueOf(rs.getByte("status")) == WasherStatus.USING &&
                    deviceStatus == WasherStatus.NOT_USING) {
                long time = System.currentTimeMillis() - rs.getTimestamp("lastUsedTime").getTime();
                if (time <= 3600000L) {
                    SqlRowSet rrs = jdbcTemplate.queryForRowSet("SELECT avgWashCount,avgWashTime FROM `config` LIMIT 1");
                    rrs.next();
                    int count = rrs.getInt("avgWashCount");
                    long newAvgTime = (rrs.getLong("avgWashTime") * count + time) / (count + 1);
                    xiaolianAnalysis.averageWashTime = newAvgTime;
                    jdbcTemplate.update("UPDATE config SET avgWashTime = ?, avgWashCount = avgWashCount + 1 LIMIT 1", newAvgTime);
                    jdbcTemplate.update("UPDATE showers SET lastWashTime = NOW() WHERE residenceId=? and deviceId = ?", residenceId, deviceId);
                    xiaolianAnalysis.residencesLastWashTime.get(residenceId).put(deviceId, System.currentTimeMillis());
                    logger.info("sql: update avgWashTime");
                } else {
                    logger.warn("sql: pass time too large: {}", time);
                }
            }
            jdbcTemplate.update("UPDATE showers SET status = ? WHERE residenceId=? and deviceId = ?", deviceObject.getInteger("deviceStatus"), residenceId, deviceId);
        } else {
            jdbcTemplate.update("INSERT INTO showers (residenceId,deviceId, location, status, lastUsedTime,lastWashTime, displayNo) VALUES (?,?, ?, ?, NOW(),NOW(), ?)",
                    residenceId,
                    deviceId,
                    deviceObject.getString("location"),
                    deviceStatus.value(),
                    deviceObject.getInteger("dispNo"));
            logger.info("sql: run sql insert into.");
        }
    }

    private void updateResidence(JSONObject postBody, int residenceId, int buildingId) {
        JSONArray deviceList = webPortal.sendPostRequest("https://netapi.xiaolianhb.com/m/net/stu/residence/listDevice",
                        postBody,
                        "https://netapi.xiaolianhb.com/2020042916153901/4.9.2.0/m/net/stu/residence/listDevice")
                .getJSONObject("data").getJSONArray("deviceInListInfo");
        if (!xiaolianAnalysis.residencesLastWashTime.containsKey(residenceId))
            xiaolianAnalysis.residencesLastWashTime.put(residenceId, new HashMap<>());
        if (!xiaolianAnalysis.residencesLastUsedTime.containsKey(residenceId))
            xiaolianAnalysis.residencesLastUsedTime.put(residenceId, new HashMap<>());
        logger.info("post http.");
        checkResidenceIndex(residenceId, buildingId);
        for (int i = 0; i < deviceList.size(); i++) {
            updateShower(deviceList.getJSONObject(i), residenceId);
        }
        if (shouldUpdateAnalysis) {
            xiaolianAnalysis.UpdateAnalysis(deviceList, residenceId);
        }
    }

    public void updateAllResidences() {
        logger.info("Update All Residences.");
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT buildingId,residenceId,floorId FROM residenceIndex");
        while (sqlRowSet.next()) {
            updateResidence(
                    XiaolianWebPortal.buildUpdateWasherRequest(
                            sqlRowSet.getInt("buildingId"), sqlRowSet.getInt("residenceId"), sqlRowSet.getInt("floorId")),
                    sqlRowSet.getInt("residenceId"),
                    sqlRowSet.getInt("buildingId"));
        }
        if (shouldUpdateAnalysis) shouldUpdateAnalysis = false;
    }
}
