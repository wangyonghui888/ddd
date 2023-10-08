package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsOMerchantsIDCode;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 操盘商户设置 Mapper 接口
 * </p>
 *
 * @author lithan
 * @since 2020-12-03
 */
@Repository
public interface RcsOperateMerchantsSetMapper extends BaseMapper<RcsOperateMerchantsSet> {


    RcsOperateMerchantsSet getOperateMerchantsSet(@Param("merchantsId") String merchantsId);

    List<RcsOMerchantsIDCode> getAllMerchantIdAndCode();
}
