package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ty_risk_tournament")
public class TyRiskTournament {

    //@TableId(value = "id")
    /**
     * 联赛ID
     */
    private String id;
    /**
     * 危险等级
     */
    private Integer riskLevel;
    /**
     * 修改时间
     */
    private String modifyDt;
    /**
     * 创建时间
     */
    private String createDt;
    /**
     * 区域id
     */
    private Integer regionId;
    /**
     * 赛种名称
     */
    private Long sportId;
    /**
     * 赛种名称
     */
    private String sportName;
    /**
     * 是否生效（是/否）
     */
    private Integer status;
    /**
     * 联赛区域
     */
    private String tournamentArea;
    /**
     * 联赛名称英文
     */
    private String tournamentNameEn;
    /**
     * 联赛名称中文简体
     */
    private String tournamentNameCn;

}
