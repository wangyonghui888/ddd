package com.panda.rcs.push.entity.vo;

import com.panda.rcs.push.entity.vo.MarketOddsPreResultMessagesVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MarketPreResultMessagesVo implements Serializable {

    private Long id;

    private Integer marketCategoryId;

    private String thirdMatchId;

    private String thirdMarketId;

    private Integer cashOutStatus;

    private Integer matchPreStatus;

    private Integer matchPreStatusRisk;

    private Integer matchPeriod;

    private Integer categoryPreStatus;

    private Integer cashOutMargin;

    private Integer status;

    private Integer marketType;

    private List<MarketOddsPreResultMessagesVo> marketOddsPreResultMessages;
}
