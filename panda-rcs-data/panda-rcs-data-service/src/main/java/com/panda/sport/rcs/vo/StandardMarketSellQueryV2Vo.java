package com.panda.sport.rcs.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 操盘手确认开售页面，前端入参接收对象
 *
 * @author carver
 */
@Data
public class StandardMarketSellQueryV2Vo implements Serializable {

    private Long sportId;

    /**
     * 标准赛事ID
     */
    @JsonProperty(value = "matchInfoId")
    private Long matchId;

    private Long playId;

    /**
     * 玩法id
     */
    private List<Long> marketCategoryIds;

    /**
     * sr权重
     */
    private Integer srWeight;

    /**
     * bc权重
     */
    private Integer bcWeight;

    /**
     * bg权重
     */
    private Integer bgWeight;

    /**
     * 开售时间
     */
    private Long sellTime;

    /**
     * 联赛等级
     */
    private Integer tournamentLevel;

    /**
     * 每页数据个数
     **/
    private Integer size = 50;
    /**
     * 从第几页开始
     **/
    private Integer page = 1;

    /*** 开始时间 历史赛事  请将参数matchStatus的值为End ***/
    private Long startTimeFrom;

    /*** 结束时间 ***/
    private Long endTimeFrom;

    /*** 是否其它早盘 ***/
    private Integer isErlyTrading;

    /*** 区域ID ***/
    private Integer regionId;

    /*** 部门ID ***/
    private String preTraderDepartmentId;

    /*** 未售Unsold，逾期未售Overdue_Unsold，申请延期  Apply_Delay，开售 Sold，取消预售  Cancel_Sold,申请取消   Apply_Cancel_Sold，停售 Stop_Sold ***/
    private String matchSellStatus;

    /*** 赛选条件  仅自己操作 userId   已开售 Sold   未开售未售 Unsold  无赛前操作手 UnPreTrader   无滚球操盘手UnLiveTrader ***/
    private String searchType;

    /** 预售id ***/
    private Long id;

    /*** 数据源编码 ***/
    private String dataSouceCode;

    /*** 当前操作人id ***/
    private String userId;

    /*** 操盘手 （查询条件）***/
    private String preTrader;

    /*** 赛事状态  '正常 Enable,移除 Move_Out,完赛 End' ***/
    private String matchStatus;

    /*** 视频源 ***/
    private String video;

    /***  视频id***/
    private String videoId;

    /***  动画源***/
    private String animation;

    /*** 动画id ***/
    private String animationId;

    /*** 视频源编码 ***/
    private String thirdVideoCode;

    /*** 是否查询收藏赛事 0 不是 1 是 ***/
    private Integer isFavorite;

    /*** 盘口类型  PRE :早盘    LIVE :滾球 */
    private String marketType;

    /*** 修改时间 */
    private Long operateTime;

    /** 用户名或用户代码 **/
    private String userCode;

    /** 判断设置的权重，生成PA赔率 **/
    private String weightType;
}
