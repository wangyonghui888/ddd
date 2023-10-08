package com.panda.sport.rcs.predict.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class VolumeControlUtils {

    @Value("${volume.control.sport.ids:1,2,3,5,7,8,9,10}")
    public String calculateSportIds;

}
