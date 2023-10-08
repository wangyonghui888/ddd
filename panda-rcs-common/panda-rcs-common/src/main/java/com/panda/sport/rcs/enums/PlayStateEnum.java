package com.panda.sport.rcs.enums;

/**
 * @author :  玩法阶段
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2020-02-22 18:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum PlayStateEnum {
    a(1, "上半场"),
    b(2, "下半场"),
    c(3, "全场"),
    d(4, "0-15分钟"),
    e(5, "单节"),
    f(6, "单盘"),
    g(7, "单局");
    private long state;
    private String vlaue;

    private PlayStateEnum(long state, String vlaue) {
        this.state = state;
        this.vlaue = vlaue;
    }
}
