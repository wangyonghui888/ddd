package com.panda.sport.rcs.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.db.entity.RiskOrderTagIp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 投注IP管理-用户IP及标签统计表 服务类
 * </p>
 *
 * @author author
 * @since 2021-02-02
 */
public interface IRiskOrderTagIpService extends IService<RiskOrderTagIp> {

    List<RiskOrderTagIp> queryListByUserId(Long[] ids, Integer count);

}
