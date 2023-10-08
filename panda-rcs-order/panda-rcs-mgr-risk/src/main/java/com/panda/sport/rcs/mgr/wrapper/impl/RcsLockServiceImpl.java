package com.panda.sport.rcs.mgr.wrapper.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.panda.sport.rcs.mapper.RcsLockMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsLockService;

/**
 * <p>
 * 服务实现类
 * </p>
 * @author admin
 * @since 2019-10-04
 */
@Service
public class RcsLockServiceImpl  implements RcsLockService {
	
	@Autowired
	private RcsLockMapper mapper;

	@Override
	public boolean saveLock(String lock) {
		try {
			if(StringUtils.isBlank(lock)) return false;
			return mapper.saveLock(lock) > 0;
		}catch (Exception e) {
			return false;
		}
	}
	
}
