package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 特殊事件配置对象
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/6 15:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RcsSpecEventConfig {
    
    /**
     * 主键
     */
    private Integer id;
    
    /**
     * 1：级别  2：联赛id   3：赛事id
     */
    private Integer type;
    
    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;
    
    /**
     * 事件名称
     */
    private String eventName;
    
    /**
     * 事件编号
     */
    private String eventCode;
    
    /**
     * 生效时间
     */
    private Long effectiveTime;
    
    /**
     * 事件级开关 1-开；0-关
     */
    private Integer eventSwitch;
    
    /**
     * 单边抽水开关 1-开；0-关
     */
    private Integer oneSideSwitch;
    
    /**
     * 是否激活当前特殊事件 1-激活；0-未激活
     */
    private Integer awayActive;
    
    /**
     * 事件激活次数
     */
    private Integer awayActiveCount;
    
    
    /**
     * 进球默认的概率
     */
    private Float awayGoalProb;
    /**
     * 是否激活当前特殊事件 1-激活；0-未激活
     */
    private Integer homeActive;
    
    /**
     * 事件激活次数
     */
    private Integer homeActiveCount;
    
    
    /**
     * 进球默认的概率
     */
    private Float homeGoalProb;


    /**
     * 操作類別編碼
     */
    @TableField(exist = false)
    private Integer operatePageCode;
    @TableField(exist = false)
    private RcsSpecEventConfig beforeParams;
}
