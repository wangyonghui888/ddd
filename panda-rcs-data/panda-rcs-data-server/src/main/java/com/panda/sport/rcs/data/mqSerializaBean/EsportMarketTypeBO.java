package com.panda.sport.rcs.data.mqSerializaBean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @ClassName EsportMarketTypeBO
 * @Author Forward
 * @DATE 2021/9/21 11:22
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class EsportMarketTypeBO extends EsportMarketType {

    /**
     * 标准赛种Id
     */
    private Long sportId;

    private List<EsportLanguageInternation> esportLanguageInternations;

}
