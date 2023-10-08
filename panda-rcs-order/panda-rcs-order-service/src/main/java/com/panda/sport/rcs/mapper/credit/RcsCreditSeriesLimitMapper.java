package com.panda.sport.rcs.mapper.credit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网串关限额
 * @Author : Paca
 * @Date : 2021-04-30 19:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsCreditSeriesLimitMapper extends BaseMapper<RcsCreditSeriesLimit> {
    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(@Param("list") List<RcsCreditSeriesLimit> list);

    @Select(" SELECT `credit_parent_id` FROM `rcs_operate_merchants_set` where merchants_id = ${creditId}  ")
    Long getMerchantIdByCreditId(@Param("creditId") String creditId);
}
