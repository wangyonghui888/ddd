package com.panda.sport.rcs.task.init;

import org.springframework.stereotype.Component;

import com.panda.sport.rcs.task.wrapper.RcsOddsConvertMappingService;

@Component
public class OddsConverMapping {
	
	public OddsConverMapping(RcsOddsConvertMappingService rcsOddsConvertMappingService) {
    	rcsOddsConvertMappingService.init();
    }

}
