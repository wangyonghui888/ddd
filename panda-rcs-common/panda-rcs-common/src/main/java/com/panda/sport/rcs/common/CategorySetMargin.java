package com.panda.sport.rcs.common;

/**
 * @author :  Felix
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.common
 * @Description :  TODO
 * @Date: 2019-10-05 10:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum CategorySetMargin {
    LIVE("live", 0),
    THIRTY_MINUTES("30m", 30),
    ONE_HOUR("1H", 60),
    TWELVE_HOURS("12H", 720),
    ONE_DAY("1D", 1440),
    TEN_DAYS("10D", 14400);
    private String timeFrame;
    private int time;
    private CategorySetMargin(String timeFrame, int time) {
        this.timeFrame = timeFrame;
        this.time = time;
    }
    public static String getTimeFrame(int time) {
        for (CategorySetMargin c : CategorySetMargin.values()) {
            if (c.getTime() == time) {
                return c.timeFrame;
            }
        }
        return null;
    }
    public String getTimeFrame() {
        return timeFrame;
    }
    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
}
