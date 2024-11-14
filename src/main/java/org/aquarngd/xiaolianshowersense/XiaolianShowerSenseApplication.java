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

    @Autowired
    public ResidenceController residenceController;

    public XiaolianShowerSenseApplication() {
        logger = LoggerFactory.getLogger(XiaolianShowerSenseApplication.class);
        logger.info("XiaolianWebHelper launched.");
    }

    public static void main(String[] args) {
        SpringApplication.run(XiaolianShowerSenseApplication.class, args);
    }

    @Async
    @Scheduled(cron = "0/10 * 13-23 * * ? ")
    public void refreshWasherDevicesData() {
        residenceController.updateAllResidences();
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void refreshAnalysis() {
        logger.info("Analysis refreshed.");
        residenceController.shouldUpdateAnalysis = true;
    }
}
