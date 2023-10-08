package com.panda.sport.rcs.log.format;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class RcsOperateSimpleLog {

    private int id;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    /**
     * 操作類別
     */
    private String operatePageName;

    /**
     * 操作對象名稱
     */
    private String objectName;

    /**
     * 操作對象擴展名稱
     */
    private String extObjectName;

    /**
     * 操作行為
     */
    @NotNull
    private String behavior;

    /**
     * 調整參數
     */
    @NotNull
    private String parameterName;
    /**
     * 修改前
     */
    @NotNull
    private String beforeVal;

    /**
     * 修改後
     */
    @NotNull
    private String afterVal;

    /**
     * 操作人Id
     */
    @NotNull
    private String userId;

    /**
     * 操作人
     */
    private String userName;

    /**
     * 操作時間
     */
    @NotNull
    private Date operateTime;
    /**
     * 操作時間字串
     */
    @TableField(exist = false)
    private String operateTimeStr;



}
