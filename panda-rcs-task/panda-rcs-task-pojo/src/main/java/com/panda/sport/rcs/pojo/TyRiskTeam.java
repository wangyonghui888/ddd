package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("ty_risk_team")
//@ApiModel(value="TyRiskTeam表对象", description="危险球队表")
@Data
public class TyRiskTeam {


    /**
     * 用户id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 危险级别
     */
    private Integer riskLevel;

    /**
     * 赛种ID
     */
    private Integer sportId;

    /**
     * 是否生效（是/否）
     */
    private Integer status;

    /**
     * 球队区域
     */
    private String teamArea;

    /**
     * 球队id
     */
    private Long teamId;

    /**
     * 球队名称中文
     */
    private String teamNameCn;

    /**
     * 球队名称英文
     */
    private String teamNameEn;

    /**
     * 赛种名称英文
     */
    private String sportNameEn;

    /**
     * 赛种名称中文简体
     */
    private String sportNameZs;

}
