package com.panda.sport.rcs.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @since 2019-10-04
 */
@Service
public interface RcsLockMapper {

	int saveLock(@Param("lock") String lock);

}
