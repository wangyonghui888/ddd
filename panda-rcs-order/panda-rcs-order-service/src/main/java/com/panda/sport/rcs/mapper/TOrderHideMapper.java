package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchProfit;
import com.panda.sport.rcs.pojo.TOrderHidePO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-07-18
 */
@Service
public interface TOrderHideMapper extends BaseMapper<TOrderHidePO> {
    @Select("SELECT NOW()")
    LocalDateTime getNowTime();

    int insertOrUpdate(TOrderHidePO orderHidePO);


    int insertOrUpdates(List<TOrderHidePO> list);
}
