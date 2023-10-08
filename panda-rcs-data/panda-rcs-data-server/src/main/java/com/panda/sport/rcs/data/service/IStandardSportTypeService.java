package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportType;

import java.util.List;

/**
 * <p>
 * 标准体育种类表.  服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-27
 */
public interface IStandardSportTypeService extends IService<StandardSportType> {

    Long getLastCrtTime();

    int insertOrUpdate(StandardSportType standardSportType);
}
