package com.panda.rcs.logService.vo;
import lombok.Data;
import java.util.List;
/**
 * @program: xindaima
 * @description:指派数据
 * @author: kimi
 * @create: 2020-11-06 17:21
 **/
@Data
public class TradingAssignmentSubPlayVo {
    private Long typeId;
    /**
     * 国际化
     */
    private Object setNames;


    List<RcsCategorySetTraderWeight> sysTraderWeightList;

}
