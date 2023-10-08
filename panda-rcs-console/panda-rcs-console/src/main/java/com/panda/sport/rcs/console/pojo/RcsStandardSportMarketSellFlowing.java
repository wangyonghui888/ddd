package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class RcsStandardSportMarketSellFlowing {
    private Long id;

    /**
    * 链路id
    */
    private String linkId;

    /**
    * 原本id
    */
    private Long oId;

    /**
    * 标准赛事id
    */
    private Long matchInfoId;

    /**
    * 赛事管理id
    */
    private String matchManageId;

    /**
    * 运动种类id
    */
    private Long sportId;

    /**
    * 是否支持滚球 1 支持  0 不支持
    */
    private Integer liveOddBusiness;

    /**
    * 联赛id
    */
    private Long tournamentId;

    /**
    * 联赛中文名
    */
    private String tournamentNameCn;

    /**
    * 联赛英文名
    */
    private String tournamentNameEn;

    /**
    * 主队id
    */
    private Long teamHomeId;

    /**
    * 主队中文名
    */
    private String teamHomeNameCn;

    /**
    * 主队英文名
    */
    private String teamHomeNameEn;

    /**
    * 客队id
    */
    private Long teamAwayId;

    /**
    * 客队中文名
    */
    private String teamAwayNameCn;

    /**
    * 客队英文名
    */
    private String teamAwayNameEn;

    /**
    * 赛前开售时间
    */
    private Long preMatchTime;

    /**
    * 滚球开售时间
    */
    private Long liveOddTime;

    /**
    * 开赛时间
    */
    private Long beginTime;

    /**
    * 赛前操盘手id
    */
    private String preTraderId;

    /**
    * 赛前操盘手
    */
    private String preTrader;

    /**
    * 赛前操盘手状态：未设置 Not_Set ，待审批 Pending_Approval ，已设置 Setted
    */
    private String preTraderStatus;

    /**
    * 滚球操盘手id
    */
    private String liveTraderId;

    /**
    * 滚球操盘手
    */
    private String liveTrader;

    /**
    * 滚球操盘手状态：未设置 Not_Set ，待审批 Pending_Approval ，已设置 Setted
    */
    private String liveTraderStatus;

    /**
    * 赛果审核员id
    */
    private String auditorId;

    /**
    * 赛果审核员
    */
    private String auditor;

    /**
    * 审核员状态：未设置 Not_Set ，待审批 Pending_Approval ，已设置 Setted
    */
    private String auditorStatus;

    /**
    * 中立场 Y 是，N 否
    */
    private String neutralGround;

    /**
    * 商业事件源编码 如：SR,BC,BG
    */
    private String businessEvent;

    /**
    * 赛前开售状态 未售Unsold，逾期未售Overdue_Unsold，申请延期 Apply_Delay，开售 Sold，申请停售 Apply_Stop_Sold，停售 Stop_Sold，意外停售 Expected_End_Sold
    */
    private String preMatchSellStatus;

    /**
    * 滚球开售状态 未售Unsold，逾期未售Overdue_Unsold，申请延期 Apply_Delay，开售 Sold，申请停售 Apply_Stop_Sold，停售 Stop_Sold，意外停售 Expected_End_Sold
    */
    private String liveMatchSellStatus;

    /**
    * 视频源 如： SR,BC,BG等
    */
    private String video;

    /**
    * 动画源 如： SR,BC,BG等
    */
    private String animation;

    /**
    * 正常 Enable，完赛 End 
    */
    private String status;

    /**
    * 显示盘口数量
    */
    private Integer marketCount;

    /**
    * 角球是否展示  0:不展示 1:展示
    */
    private Boolean cornerShow;

    /**
    * 罚牌是否展示  0:不展示 1:展示
    */
    private Boolean cardShow;

    /**
    * 创建时间
    */
    private Long createTime;

    /**
    * 修改时间
    */
    private Long modifyTime;

    /**
    * 赛前操盘平台 如 SR MTS等
    */
    private String preRiskManagerCode;

    /**
    * 滚球操盘平台
    */
    private String liveRiskManagerCode;

    /**
    * 赛前数据服务商
    */
    private String preMatchDataProviderCode;

    /**
    * 滚球数据服务商
    */
    private String liveMatchDataProviderCode;

    private String platform;

    /**
    * 联赛级别
    */
    private Integer tournamentLevel;

    /**
    * 局数(赛制).数字,例如:5,7,代表最多打5局7局
    */
    private Integer roundType;

    /**
    * 赛前操盘手部门
    */
    private Integer preTraderDepartmentId;

    /**
    * 滚球操盘手部门
    */
    private Integer liveTraderDepartmentId;

    /**
    * 审核员部门
    */
    private Integer auditorDepartmentId;

    private String videoId;

    private String animationId;

    private String insertTime;
}