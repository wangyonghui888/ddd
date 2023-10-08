package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsCategorySetTraderWeight;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RcsCategorySetTraderWeightMapper extends BaseMapper<RcsCategorySetTraderWeight> {

    int insertOrUpdate(RcsCategorySetTraderWeight record);

    int batchInsertOrUpdate(@Param("list") List<RcsCategorySetTraderWeight> list);


}