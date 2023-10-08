package com.panda.rcs.sdk.env;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;

@Component
public class GuicePropertySource implements ApplicationContextInitializer<ConfigurableApplicationContext>{
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		PropertiesUtil utile = GuiceContext.getInstance(PropertiesUtil.class);
		Map<String, Object> map = new HashMap<String, Object>();
		for (String name: utile.getProperties().stringPropertyNames()) {
			map.put(name, utile.getProperties().getProperty(name));
		}
		PropertySource<Map<String, Object>> propertySource = new MapPropertySource("GuicePropertySource", map);
		applicationContext.getEnvironment().getPropertySources().addLast(propertySource);
	}

}
