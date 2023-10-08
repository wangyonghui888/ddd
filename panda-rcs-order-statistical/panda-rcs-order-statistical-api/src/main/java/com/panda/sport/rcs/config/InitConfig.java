package com.panda.sport.rcs.config;


import com.panda.sport.rcs.common.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author lithan
 */
@Component
public class InitConfig {

    @Value("${decimal.precision:2}")
    int precision;

    @PostConstruct
    public void init() {
        Constants.PRECISION = precision;
    }
}