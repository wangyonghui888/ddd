package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  订单明细扩展表
 * @Date: 2020-01-31 11:43
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Mapper
public interface TOrderDetailExtMapper extends BaseMapper<TOrderDetailExt> {
    /**
     * @Description   //根据接拒单配置更新订单等待时间
     * @Param [config]
     * @Author  sean
     * @Date   2020/11/7
     * @return void
     **/
    Integer updateOrderEextWaitTime(@Param("config") RcsTournamentTemplateAcceptConfig config,@Param("currentTime") Long currentTime);
}
