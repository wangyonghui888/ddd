package com.panda.rcs.order.entity.bean;

import com.panda.rcs.order.entity.vo.OrderBetRecordNotifyVO;
import lombok.Data;

import java.util.List;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.websocket.bean
 * @Description :  注单版本号
 * @Date: 2019-11-07 11:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class OrderBetRecordNotifySocketBean extends OrderBetRecordNotifyVO {
    /**
     * @Description   0今日 1 早盘 2滚球  全部  -1
     * @Param 
     * @Author  Sean
     * @Date  16:52 2020/1/14
     * @return 
     **/
    private Integer matchDate;
    /**
     * @Description   赛事开始时间
     * @Param
     * @Author  Sean
     * @Date  16:52 2020/1/14
     * @return
     **/
    private Long matchStartDate;
    /**
     * @Description   赛事结束时间
     * @Param
     * @Author  Sean
     * @Date  16:52 2020/1/14
     * @return
     **/
    private Long matchEndDate;
    /**
     * @Description   玩法阶段
     * @Param 
     * @Author  Sean
     * @Date  17:00 2020/1/16
     * @return 
     **/
    private Integer playPhaseType;
    /**
     * 接单模式 0 自动 1 手动
     */
    private Integer tradeType;

    /**
     * 入口类型
     * 1:手动注单
     * 2:及时注单
     * 3、赛事注单
     */
    private Integer inputType;
    
    private String sportId;
    /**
     * @Description   //串关类型
     * @Param
     * @Author  sean
     * @Date   2020/11/25
     * @return
     **/
    private Integer seriesType;

    /**
     * 过关类型  0全部 1单关 2串关
     */
    private Integer passType;

    /**
     * 是否暂停显示新注单 1是 2不是
     */
    private Integer isPause;
    /**
     * 商户Id
     */
    private List<String> merchantIds;
    /**
     * en 英文 zs 简体 zh 繁体
     */
    private String languageType;

    private Long truserId;

    private String truserName;

    /**
     * 获取
     * @return
     */
    public Integer getInputType(){
        if(inputType == null){
            return 0;
        }
        return inputType;
    }

}
