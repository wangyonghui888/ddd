package com.panda.sport.rcs.common.vo.rule;

/**
 * D4	篮球打洞	篮球投注时，在同一赛事相同玩法的不同盘口之间交叉下注（无论是否投注成功），
 * 相应注单标记为篮球打洞。涉及玩法PDID：2（常规赛总分）、10（常规赛{主队}总分）、
 * 11（常规赛{客队}总分）、18（上半场总分）、26（下半场总分）、
 * 38（总分）、45（第1节总分）、51（第2节总分）、57（第3节总分）、
 * 63（第4节总分）、87（上半场{主队}总分）、88（下半场{主队}总分）、
 * 97（上半场{客队}总分）、98（下半场{客队}总分）、
 * 145（第{X}节{主队}总分）、146（第{X}节{客队}总分）
 *
 * @author lithan
 * @date 2020-07-10 10:40:22
 */
public class DangerousR4Vo {
    /**
     * 玩法id
     */
    public Long playId;
    /**
     * 盘口数
     */
    public Long marketNum;

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Long getMarketNum() {
        return marketNum;
    }

    public void setMarketNum(Long marketNum) {
        this.marketNum = marketNum;
    }
}
