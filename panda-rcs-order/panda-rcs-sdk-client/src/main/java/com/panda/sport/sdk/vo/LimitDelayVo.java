package com.panda.sport.sdk.vo;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.SettleItem;

/**
 * 商户单日限额 延迟入库
 */
public class LimitDelayVo {

    String busId;

    String userId;

    String sportId;

    String userSportKey;

    ExtendBean extendBean;

    SettleItem settleItem;

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getUserSportKey() {
        return userSportKey;
    }

    public void setUserSportKey(String userSportKey) {
        this.userSportKey = userSportKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public ExtendBean getExtendBean() {
        return extendBean;
    }

    public void setExtendBean(ExtendBean extendBean) {
        this.extendBean = extendBean;
    }

    public SettleItem getSettleItem() {
        return settleItem;
    }

    public void setSettleItem(SettleItem settleItem) {
        this.settleItem = settleItem;
    }
}
