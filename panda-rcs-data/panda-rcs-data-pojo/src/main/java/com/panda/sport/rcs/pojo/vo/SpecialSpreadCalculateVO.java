package com.panda.sport.rcs.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 盘口设置表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpecialSpreadCalculateVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 类型：1 ：早盘 ，0： 滚球盘， 3： 冠军盘
     */
    private Integer matchType;

    private List<SpecialSpreadCalculatePlayVO> plays;
}
