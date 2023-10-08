package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketOddsNameI18nList extends RcsBaseEntity<MarketOddsNameI18nList> {

    private String nameCode;

    /**
     * 1 人工  2 系统
     * 人工不允许数据商下发的数据覆盖
     */
    private Integer flag;

    private String dataSourceCode;

    private String languageType;

    private String text;

    private String remark;

    private Long createTime;

    private Long modifyTime;

}
