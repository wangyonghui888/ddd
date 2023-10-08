package com.panda.sport.rcs.pojo.statistics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 赛事玩法初盘表
 * </p>
 *
 * @author lithan
 * @since 2021-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsFirstMarket implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;

    /**
     * 玩法id
     */
    private Integer playId;

    /**
     * 1:初盘 2:赛前终盘
     */
    private Integer type;

    /**
     * 值
     */
    private String value;

    private Date createTime;


}
