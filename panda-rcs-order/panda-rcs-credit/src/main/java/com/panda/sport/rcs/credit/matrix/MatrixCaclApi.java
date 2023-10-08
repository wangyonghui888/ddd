package com.panda.sport.rcs.credit.matrix;

import com.panda.sport.data.rcs.dto.ExtendBean;

public interface MatrixCaclApi {
	
	/**
	 * 获取当前比分计算的结果
	 * 1 输 2:输半  3 :赢  4：赢半  5:平
	 * @param homeScore  主队比分
	 * @param awayScore  客队比分
	 * @param bean
	 * @return
	 */
	Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean);



    /**
     * 根据当前主客比分和 投注项编码,计算当前输赢结果
     * @description
     * @param m             主队比分
     * @param n             客队比分
     * @param templateCode  投注项id
     * @return java.lang.Integer
     * @author dorich
     * @date 2020/3/20 16:02
     **/
    Integer getSettleResult(int m, int n, String templateCode);

    /**
     * 该方法抽象出来用于计算存在1/4盘的玩法
     *
     * @param m           主队比分
     * @param n           客队比分
     * @param marketValue 盘口值
     * @param upper        用于区分两项盘的盘口. true代表一个盘口;false代表另外一个盘口
     * @return int
     * @description
     * @author dorich
     * @date 2020/4/5 13:18
     **/
    int calculateSelect(int m, int n, String marketValue, boolean upper);
}
