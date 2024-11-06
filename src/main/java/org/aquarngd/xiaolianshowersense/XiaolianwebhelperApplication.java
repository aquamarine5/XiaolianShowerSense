package org.aquarngd.xiaolianshowersense;
//Mybatis

import org.aquarngd.xiaolianshowersense.data.ResidenceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
@EnableJpaRepositories(basePackages = "org.aquarngd.xiaolianshowersense.data")
@EntityScan(basePackages = "org.aquarngd.xiaolianwebhelper.data")
@EnableScheduling
@SpringBootApplication
public class XiaolianwebhelperApplication {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    Logger logger;

    @Autowired
    public XiaolianWebPortal webPortal;

    @Autowired
    public ResidenceController residenceController;

    public XiaolianwebhelperApplication() {
        logger = LoggerFactory.getLogger(XiaolianwebhelperApplication.class);
        logger.info("XiaolianWebHelper launched.");
    }

    public static void main(String[] args) {
        SpringApplication.run(XiaolianwebhelperApplication.class, args);
    }

    @Async
    @Scheduled(cron = "0/10 * 13-22 * * ? ")
    public void refreshWasherDevicesData() {
        residenceController.updateAllResidences();
    }
}
