package com.panda.sport.rcs.log.format;

import com.baomidou.mybatisplus.annotation.TableField;
import com.panda.sport.rcs.log.annotion.OperateLog;
import com.panda.sport.rcs.utils.OperateLogUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = false)
public class RcsOperateLog {

    private int id;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    /**
     * 操作類別
     */
    @TableField(exist = false)
    private String operatePageName;

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
    @NotNull
    private String behavior;

    /**
     * 操作參數
     */
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

    /**
     * 球种名称
     */
    @TableField(exist = false)
    private String sportName;

    /**
     * 賽事Id
     */
    private Long matchId;

    /**
     * 球种ID
     */
    private Integer sportId;

    /**
     * 玩法Id
     */
    private Long playId;
    /**
     * 操作人ip
     */
    private String ip;

    public RcsOperateLog() {
    }

    public RcsOperateLog(OperateLog operateLog) {
        this.behavior = operateLog.operateType().getName();
        this.parameterName = operateLog.operateParamter().getName();
        this.userId = OperateLogUtils.getUserId();
        this.operateTime = OperateLogUtils.getLocalTimes();
        this.ip = OperateLogUtils.getIpAddr();
    }

    //針對Object轉換String的Set方法

    public void setObjectIdByObj(Object objectId) {
        this.objectId =  Objects.toString(objectId,"");
    }

    public void setObjectNameByObj(Object objectName) {
        this.objectName = Objects.toString(objectName,"");
    }

    public void setExtObjectIdByObj(Object extObjectId) {
        this.extObjectId = Objects.toString(extObjectId,"");
    }

    public void setExtObjectNameByObj(Object extObjectName) {
        this.extObjectName = Objects.toString(extObjectName,"");
    }

    public void setBeforeValByObj(Object beforeVal) {
        this.beforeVal = Objects.toString(beforeVal,"");
    }

    public void setAfterValByObj(Object afterVal) {
        this.afterVal = Objects.toString(afterVal,"");
    }
}
