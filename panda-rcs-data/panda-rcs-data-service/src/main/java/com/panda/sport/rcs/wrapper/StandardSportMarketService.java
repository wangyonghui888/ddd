package com.panda.sport.rcs.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportMarket;

import java.util.Map;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 标准盘口信息
 * @Author : Paca
 * @Date : 2020-11-25 13:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface StandardSportMarketService extends IService<StandardSportMarket> {
    /**
     * 获取赛事盘口的基本信息
     *
     * @param map
     * @return
     */
    Map<String, Object> queryMatchMarketInfo(Map<String, Object> map);

    /**
     * 通过模板id查询nameCode
     *
     * @param oddsFieldsTemplateId
     * @return
     */
    String queryOddTemplateInfo(Long oddsFieldsTemplateId);
}
