package com.panda.rcs.push.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Slf4j
@Configuration
@EnableScheduling
@EnableAsync
public class ClientCleanupJob {

    @Scheduled(cron = "0 0/30 * * * ?")
    private void finishMatch(){

    }

}
