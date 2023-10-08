package com.panda.sport.rcs.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author :  Administrator
 * @Project Name :  panda-rcs-trade-group
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :  TODO
 * @Date: 2020-07-31 14:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MarketOddsMongo {
    private static final long serialVersionUID = 1L;

    @Field(value = "id")
    private Long id;

    private String fieldOddsValue;


    private String money;

    private String nameExpressionValue;

    private String oddsType;

    private Integer active;

}
