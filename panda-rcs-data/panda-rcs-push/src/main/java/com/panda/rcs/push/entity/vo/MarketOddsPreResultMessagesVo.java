package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MarketOddsPreResultMessagesVo implements Serializable {

    private Long id;

    private String thirdOddsFieldSourceId;

    private String oddsType;

    private String probabilities;

    private String winPro;

    private String losePro;

    private String halfWinPro;

    private String refundPro;

    private String loseWinPro;
}
