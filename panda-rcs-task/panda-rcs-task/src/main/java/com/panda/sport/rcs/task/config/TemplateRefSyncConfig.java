package com.panda.sport.rcs.task.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
@Data
public class TemplateRefSyncConfig {
    @Value("${task.ref.basketballSyncEvent.matchIds}")
    private String syncEventMatchIds;
}
