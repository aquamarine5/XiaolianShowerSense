package org.aquarngd.xiaolianshowersense;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.aquarngd.xiaolianshowersense.data.WasherStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;

@Component
public class XiaolianAnalysis {

    Logger logger;
    final
    JdbcTemplate jdbcTemplate;

    public long averageWashTime = 0L;

    Map<Integer, List<List<Integer>>> residencesMap = new HashMap<>();

    Map<Integer, Map<Integer, Long>> residencesLastWashTime = new HashMap<>();

    Map<Integer, Map<Integer, Long>> residencesLastUsedTime = new HashMap<>();

    public XiaolianAnalysis(JdbcTemplate jdbcTemplate) {
        logger = LoggerFactory.getLogger(XiaolianAnalysis.class);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT residenceId,mapdata FROM residenceIndex");
        while (sqlRowSet.next()) {
            String mapdataString = sqlRowSet.getString("mapdata");
            if (mapdataString == null) {
                continue;
            }
            int residenceId = sqlRowSet.getInt("residenceId");
            JSONArray mapData = JSONArray.parseArray(mapdataString);
            List<List<Integer>> mapAllRoadData = new ArrayList<>();
            for (int i = 0; i < mapData.size(); i++) {
                List<Integer> mapDataList = mapData.getJSONArray(i).getJSONArray(0).toJavaList(Integer.class);
                mapDataList.addAll(mapData.getJSONArray(i).getJSONArray(1).toJavaList(Integer.class));
                mapDataList.removeIf(value -> value == -1);
                mapAllRoadData.add(mapDataList);
            }
            residencesMap.put(residenceId, mapAllRoadData);
            if (!residencesLastWashTime.containsKey(residenceId)) {
                residencesLastWashTime.put(residenceId, new HashMap<>());
            }
            if (!residencesLastUsedTime.containsKey(residenceId)) {
                residencesLastUsedTime.put(residenceId, new HashMap<>());
            }
            SqlRowSet residenceTimes = jdbcTemplate.queryForRowSet(
                    "SELECT lastUsedTime,lastWashTime,deviceId FROM showers WHERE residenceId=?", residenceId);
            while (residenceTimes.next()) {
                residencesLastWashTime.get(residenceId).put(residenceTimes.getInt("deviceId"),
                        residenceTimes.getTimestamp("lastWashTime").getTime());
                residencesLastUsedTime.get(residenceId).put(residenceTimes.getInt("deviceId"),
                        residenceTimes.getTimestamp("lastUsedTime").getTime());
            }
        }
        logger.warn("residencesMap: {}", residencesMap);
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean isEnabled() {
        String sql = "SELECT isAnalysisEnabled FROM config LIMIT 1";
        Boolean isEnabled = jdbcTemplate.queryForObject(sql, Boolean.class);
        return isEnabled != null && isEnabled;
    }

    public void UpdateAnalysis(JSONArray residences, int residenceId) {
        if (!isEnabled()) {
            logger.warn("Analysis is disabled.");
            return;
        }
        if (!residencesMap.containsKey(residenceId)) return;
        CheckDatabase(residenceId);
        Map<Integer, Integer> predictedRoadTime = new HashMap<>();
        for (int i = 0; i < residences.size(); i++) {
            JSONObject washer = residences.getJSONObject(i);
            int deviceId = washer.getInteger("deviceId");
            int calculatedTime = 0;
            if (washer.getInteger("deviceStatus") == WasherStatus.USING.value()) {
                long predictedTime = averageWashTime -
                        (System.currentTimeMillis() - residencesLastUsedTime.get(residenceId).get(deviceId));
                calculatedTime = predictedTime < 0 ? 5 * 60 : (int) (predictedTime / 1000);
            } else if (washer.getInteger("deviceStatus") == WasherStatus.NOT_USING.value()) {
                if (System.currentTimeMillis() - residencesLastWashTime.get(residenceId).get(deviceId) <= 360000) {
                    calculatedTime = 60;
                }
            } else {
                calculatedTime = 3 * 60;
            }
            predictedRoadTime.put(washer.getInteger("dispNo"), calculatedTime);
        }
        logger.warn("predictedRoadTime: {}", predictedRoadTime);
        List<Integer> allPredictedTime = new ArrayList<>();
        for (int i = 0; i < residencesMap.get(residenceId).size(); i++) {
            int roadPredictedTime = 0;
            for (int j = 0; j < residencesMap.get(residenceId).get(i).size(); j++) {
                roadPredictedTime += predictedRoadTime.get(residencesMap.get(residenceId).get(i).get(j));
            }
            allPredictedTime.add(roadPredictedTime);
        }
        LocalTime currentTime = LocalTime.now();
        String valueName = String.format("3%s%s",
                currentTime.getHour() < 10 ? "0" + currentTime.getHour() : currentTime.getHour(),
                currentTime.getMinute() < 10 ? "00" : (currentTime.getMinute() / 10) * 10);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("""
                SELECT minv,avgv,maxv,count
                FROM analyses WHERE residenceId=? and timePos=?""", residenceId, valueName);

        if (sqlRowSet.next()) {
            int count = sqlRowSet.getInt("count");
            int currentMinData = sqlRowSet.getInt("minv");
            int currentAvgData = sqlRowSet.getInt("avgv");
            int currentMaxData = sqlRowSet.getInt("maxv");

            jdbcTemplate.update("UPDATE analyses SET minv = ?, avgv = ?, maxv = ?, count = count+1 WHERE residenceId=? and timePos = ?",
                    residenceId,
                    (currentMinData * count + allPredictedTime.stream().min(Integer::compareTo).orElse(currentMinData)) / (count + 1),
                    (currentAvgData * count + allPredictedTime.stream().mapToInt(Integer::intValue).sum() / allPredictedTime.size()) / (count + 1),
                    (currentMaxData * count + allPredictedTime.stream().max(Integer::compareTo).orElse(currentMaxData)) / (count + 1),
                    valueName);
        }
    }

    private boolean isResidenceIdInAnalyses(int residenceId) {
        String sql = "SELECT COUNT(*) FROM analyses WHERE residenceId = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, residenceId);
        return count != null && count > 0;
    }

    private void CheckDatabase(int residenceId) {
        if (!isResidenceIdInAnalyses(residenceId)) {
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 6; j++) {
                    String valueName = String.format("3%s%s",
                            i < 10 ? "0" + i : i,
                            j == 0 ? "00" : j * 10);
                    jdbcTemplate.update(
                            "INSERT INTO analyses (minv,avgv,maxv,timePos,residenceId) VALUES (0,0,0,?,?)", valueName, residenceId);
                }
            }
        }
    }
}
