package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ty_risk_user_group")
public class RiskUserGroup {

    /**
     * 用户id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 危险等级
     */
    private Integer dangerLevel;
    /**
     * 操作时间
     */
    private String modifyDt;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 玩家组名称
     */
    private String userGroupName;
    /**
     * 包含用户数
     */
    private Integer userCount;
    /**
     * 备注/理由
     */
    private String remark;
}
