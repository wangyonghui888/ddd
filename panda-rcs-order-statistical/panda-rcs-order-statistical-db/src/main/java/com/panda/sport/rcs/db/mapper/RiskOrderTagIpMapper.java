package com.panda.sport.rcs.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.db.entity.RiskOrderTagIp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 投注IP管理-用户IP及标签统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2021-02-02
 */
public interface RiskOrderTagIpMapper extends BaseMapper<RiskOrderTagIp> {

    List<RiskOrderTagIp> queryListByUserId(@Param("ids") Long[] ids, @Param("count") Integer count);

}
