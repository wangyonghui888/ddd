package com.panda.sport.data.rcs.api;

public interface PIMtsApiService {
  /** 賽事 ID 轉成資料商的賽事 ID */
  Response<Long> getThirdMatchSourceId(Long matchId);

  /** 盤口 ID 轉成資料商的盤口 ID */
  Response<Long> getThirdMarketSourceId(Long marketId);

  /**
   * 投注項 ID 轉成資料商的投注項 ID
   */
  Response<String> getThirdPlayOptionSourceId(Long playOptionId);

  Response<String> getMaxBetAmount(Long marketId, Long playOptionId);
}
