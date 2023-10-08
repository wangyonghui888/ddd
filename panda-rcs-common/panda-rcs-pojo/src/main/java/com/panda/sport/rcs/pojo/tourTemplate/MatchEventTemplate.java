package com.panda.sport.rcs.pojo.tourTemplate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  事件模板表
 * @Date: 2020-07-14 20:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchEventTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 运动ID
     */
    private Integer sportId;
    /**
     *序号
     */
    private Integer orderNo;
    /**
     * 触发事件编码
     */
    private String triggerCode;
    /**
     * 触发事件阶段ID
     */
    private String triggerPeriodId;
    /**
     * 事件编码
     */
    private String eventCode;
    /**
     * 事件文本模板
     */
    private String templateText;
    /**
     * 事件格式化模板
     */
    private String templateFormat;
    /**
     * 赛事事件模板号1阶段2次序3开球
     */
    private String templateNo;
    /**
     * 事件审核倒计时时间默认值
     */
    private Integer auditTime;
    /**
     * 事件结算倒计时时间默认值
     */
    private Integer billTime;
}
