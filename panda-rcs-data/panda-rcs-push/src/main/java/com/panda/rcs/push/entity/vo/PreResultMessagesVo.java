package com.panda.rcs.push.entity.vo;

import com.panda.rcs.push.entity.vo.MarketPreResultMessagesVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PreResultMessagesVo implements Serializable {

    private String standardMatchInfoId;

    private Integer matchType;

    private String dataSourceCode;

    private Long modifyTime;

    private Integer sportId;

    private Integer matchPreStatus;

    private List<MarketPreResultMessagesVo> marketPreResultMessages;
}
