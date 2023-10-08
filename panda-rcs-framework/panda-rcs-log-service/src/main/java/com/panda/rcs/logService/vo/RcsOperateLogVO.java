package com.panda.rcs.logService.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class RcsOperateLogVO {

    /**
     * 操作頁面
     */
    private Integer operatePageCode;

    /**
     * 操作頁面陣列
     */
    private List<Integer> operatePageCodes;

    /**
     * 操作對象ID
     */
    private String objectId;

    /**
     * 操作對象名稱
     */
    private String objectName;

    /**
     * 操作對象擴展ID
     */
    private String extObjectId;

    /**
     * 操作對象擴展名稱
     */
    private String extObjectName;

    /**
     * 操作行為
     */
    private String behavior;

    /**
     * 操作人Id
     */
    private String userId;

    /**
     * 操作人名稱
     */
    private String userName;

    /**
     * 起始時間
     */
    private String operateStartTime;

    /**
     * 結束時間
     */
    private String operateEndTime;

    /**
     * 球種ID
     */
    private Integer sportId;

    /**
     * 賽事Id
     */
    private Integer matchId;


    private Long matchManageId;


    /**
     * 賽事類型 1：早盘；0：滚球盘。
     */
    private Integer matchType;

    /**
     * 玩法Id
     */
    private Integer playId;

    /**
     * 玩法 集合
     */
    private List<Long> playIds;

    /**
     * 玩法集ID
     */
    private Long categorySetId;

    /**
     * 頁碼
     */
    private Integer pageNum;

    /**
     * 每頁數量
     */
    private Integer pageSize;
    /**
     * 语言
     */
    private String lang;

    public RcsOperateLogVO() {
    }

}
