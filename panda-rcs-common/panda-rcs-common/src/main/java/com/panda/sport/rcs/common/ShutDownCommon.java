package com.panda.sport.rcs.common;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ShutDownCommon implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {

        	@Override
        	public void run() {
        		log.warn("收到退出信号，JVM退出");
        	}
        });
	}

}
