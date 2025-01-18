package org.aquarngd.xiaolianshowersense;
//Mybatis

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableAsync
@EntityScan(basePackages = "org.aquarngd.xiaolianshowersense")
@EnableScheduling
@SpringBootApplication
public class XiaolianShowerSenseApplication {

    Logger logger;

    private final XiaolianWebPortal xiaolianWebPortal;
    private final ResidenceController residenceController;
    private final JdbcTemplate jdbcTemplate;

    public XiaolianShowerSenseApplication(ResidenceController residenceController, JdbcTemplate jdbcTemplate, XiaolianWebPortal xiaolianWebPortal) {
        logger = LoggerFactory.getLogger(XiaolianShowerSenseApplication.class);
        logger.info("XiaolianWebHelper launched.");
        this.residenceController = residenceController;
        this.jdbcTemplate = jdbcTemplate;
        if (isDatabaseEmpty())
            setupDatabase();
        this.xiaolianWebPortal = xiaolianWebPortal;
    }

    public static void main(String[] args) {
        SpringApplication.run(XiaolianShowerSenseApplication.class, args);
    }

    @Async
    @Scheduled(cron = "0/10 * 13-22 * * ? ")
    public void refreshWasherDevicesData() {
        residenceController.updateAllResidences();
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void refreshAnalysis() {
        logger.info("Analysis refreshed.");
        residenceController.shouldUpdateAnalysis = true;
    }

    public boolean isDatabaseEmpty() {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'xiaolian'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null || count == 0;
    }

    public void setupDatabase() {
        jdbcTemplate.execute("""
                CREATE TABLE xiaolian.residenceIndex (
                    residenceId INT PRIMARY KEY,
                    floorId INT NOT NULL,
                    buildingId INT NOT NULL,
                    name TEXT NOT NULL,
                    mapdata JSON,
                    contributors TEXT,
                    analyseStartTime MEDIUMINT UNSIGNED,
                    analyseEndTime MEDIUMINT UNSIGNED
                ) CHARACTER SET utf8mb4""");
        jdbcTemplate.execute("""
                CREATE TABLE xiaolian.showers (
                    deviceId INT PRIMARY KEY,
                    location VARCHAR(50) NOT NULL,
                    displayNo TINYINT(3) NOT NULL,
                    status TINYINT(2) NOT NULL,
                    lastUsedTime TIMESTAMP NOT NULL,
                    lastWashTime TIMESTAMP NOT NULL,
                    residenceId INT NOT NULL
                ) CHARACTER SET utf8mb4""");
        jdbcTemplate.execute("""
                CREATE TABLE xiaolian.analyses (
                    minv SMALLINT UNSIGNED NOT NULL DEFAULT 0,
                    avgv SMALLINT UNSIGNED NOT NULL DEFAULT 0,
                    maxv SMALLINT UNSIGNED NOT NULL DEFAULT 0,
                    timePos SMALLINT UNSIGNED NOT NULL,
                    count SMALLINT UNSIGNED NOT NULL DEFAULT 0,
                    residenceId INT UNSIGNED NOT NULL,
                    PRIMARY KEY (residenceId, timePos)
                ) CHARACTER SET utf8mb4""");
        jdbcTemplate.execute("""
                CREATE TABLE xiaolian.config (
                    accessToken TEXT NOT NULL,
                    refreshToken TEXT NOT NULL,
                    avgWashCount MEDIUMINT UNSIGNED DEFAULT 0,
                    avgWashTime MEDIUMINT UNSIGNED DEFAULT 0,
                    requestTimes MEDIUMINT UNSIGNED DEFAULT 0,
                    isAnalysisEnabled BOOLEAN DEFAULT TRUE NOT NULL
                ) CHARACTER SET utf8mb4""");
        if (xiaolianWebPortal != null) {
            xiaolianWebPortal.relogin();
        }
        residenceController.setupAllSupportedResidenceIndex();
    }
}
