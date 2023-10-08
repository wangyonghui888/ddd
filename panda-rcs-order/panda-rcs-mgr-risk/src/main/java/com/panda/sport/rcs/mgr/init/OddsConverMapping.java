package com.panda.sport.rcs.mgr.init;

import org.springframework.stereotype.Component;

import com.panda.sport.rcs.mgr.wrapper.impl.RcsOddsConvertMappingServiceImpl;

@Component
public class OddsConverMapping {
	
	public OddsConverMapping(RcsOddsConvertMappingServiceImpl rcsOddsConvertMappingService) {
    	rcsOddsConvertMappingService.init();
    }

}
