package org.aquarngd.xiaolianshowersense;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT residenceId,mapdata FROM `residenceIndex`");
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
            SqlRowSet residenceTimes = jdbcTemplate.queryForRowSet("SELECT lastUsedTime,lastWashTime,deviceId FROM `" + sqlRowSet.getInt("residenceId") + "`");
            while (residenceTimes.next()) {
                residencesLastWashTime.get(residenceId).put(residenceTimes.getInt("deviceId"), residenceTimes.getTimestamp("lastWashTime").getTime());
                residencesLastUsedTime.get(residenceId).put(residenceTimes.getInt("deviceId"), residenceTimes.getTimestamp("lastUsedTime").getTime());
            }
        }
        logger.warn("residencesMap: {}", residencesMap);
        this.jdbcTemplate = jdbcTemplate;
    }

    public void UpdateAnalysis(JSONArray residences, int residenceId) {
        logger.warn("residences: {}", residenceId);
        if (!residencesMap.containsKey(residenceId)) return;
        logger.warn("residences: {}", residenceId);
        CheckDatabase(residenceId);
        Map<Integer, Integer> predictedRoadTime = new HashMap<>();
        for (int i = 0; i < residences.size(); i++) {
            JSONObject washer = residences.getJSONObject(i);
            int deviceId = washer.getInteger("deviceId");
            int calculatedTime = 0;
            if (washer.getInteger("deviceStatus") == 2) {
                long predictedTime = averageWashTime - (System.currentTimeMillis() - residencesLastUsedTime.get(residenceId).get(deviceId));
                calculatedTime = predictedTime < 0 ? 5 * 60 : (int) (predictedTime / 1000);
            } else if (washer.getInteger("deviceStatus") == 1) {
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
        String valueName = String.format("8%s%s",
                currentTime.getHour() < 10 ? "0" + currentTime.getHour() : currentTime.getHour(),
                currentTime.getMinute() < 10 ? "00" : (currentTime.getMinute() / 10) * 10);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT " +
                "minv,avgv,maxv" +
                " FROM `" + residenceId + "_analysis` WHERE timePos=" + valueName);
        logger.warn("allPredictedTime: {}", allPredictedTime);
        logger.warn(String.valueOf(allPredictedTime.stream().min(Integer::compareTo).orElse(0)));
        logger.warn(String.valueOf(allPredictedTime.stream().mapToInt(Integer::intValue).sum() / allPredictedTime.size()));

        logger.warn(String.valueOf(allPredictedTime.stream().max(Integer::compareTo).orElse(0)));
        if (sqlRowSet.next()) {
            int currentMinData = sqlRowSet.getInt("minv");
            if (currentMinData == 0)
                jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET minv = " +
                        allPredictedTime.stream().min(Integer::compareTo).orElse(0) +
                        " WHERE timePos=" + valueName);
            int currentAvgData = sqlRowSet.getInt("avgv");
            if (currentAvgData == 0)
                jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET avgv = " +
                        allPredictedTime.stream().mapToInt(Integer::intValue).sum() / allPredictedTime.size() +
                        " WHERE timePos=" + valueName);
            int currentMaxData = sqlRowSet.getInt("maxv");
            if (currentMaxData == 0)
                jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET maxv = " +
                        allPredictedTime.stream().max(Integer::compareTo).orElse(0) +
                        " WHERE timePos=" + valueName);

            jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET minv = " +
                    (currentMinData + allPredictedTime.stream().min(Integer::compareTo).orElse(currentMinData)) / 2 +
                    " WHERE timePos=" + valueName);
            jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET avgv = " +
                    (currentAvgData + allPredictedTime.stream().mapToInt(Integer::intValue).sum() / allPredictedTime.size()) / 2 +
                    " WHERE timePos=" + valueName);
            jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET maxv = " +
                    (currentMaxData + allPredictedTime.stream().max(Integer::compareTo).orElse(currentMaxData)) / 2 +
                    " WHERE timePos=" + valueName);
        } else {
            jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET minv = " +
                    allPredictedTime.stream().min(Integer::compareTo).orElse(0) +
                    " WHERE timePos=" + valueName);
            jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET avgv = " +
                    allPredictedTime.stream().mapToInt(Integer::intValue).sum() / allPredictedTime.size() +
                    " WHERE timePos=" + valueName);
            jdbcTemplate.execute("UPDATE `" + residenceId + "_analysis` SET maxv = " +
                    allPredictedTime.stream().max(Integer::compareTo).orElse(0) +
                    " WHERE timePos=" + valueName);
        }
    }

    private void CheckDatabase(int residenceId) {
        if (!isDatabaseExisted(String.format("%d_analysis", residenceId))) {
            jdbcTemplate.execute("CREATE TABLE `" + residenceId + "_analysis` (" + """
                    minv INT,
                    avgv INT,
                    maxv INT,
                    timePos INT PRIMARY KEY
                    ) CHARACTER SET utf8mb4;
                    """);
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 6; j++) {
                    String valueName = String.format("8%s%s",
                            i < 10 ? "0" + i : i,
                            j == 0 ? "00" : j * 10);
                    jdbcTemplate.execute("INSERT INTO `" + residenceId + "_analysis` (minv,avgv,maxv,timePos) VALUES (0,0,0," + valueName + ")");
                }
            }
        }
    }

    private boolean isDatabaseExisted(String id) {
        Connection connection = null;
        ResultSet rs = null;
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
