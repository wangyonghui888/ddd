package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 标准体育区域表 服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardSportRegionService extends IService<StandardSportRegion> {
    /**
     * @MethodName:
     * @Description: 得到最后的插入时间
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/30
     **/
    Long getLastCrtTime();

    int batchInsert(List<StandardSportRegion> standardSportRegions);

    List<StandardSportRegion> listByListIds(ArrayList<Long> sportRegionLongs);

    int batchInsertOrUpdate(List<StandardSportRegion> standardSportRegions);
}
