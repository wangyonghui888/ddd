package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 批量切换数据源记录
 * magic
 * 2023.4.19
 */
@Data
public class RcsBatchChangeDataSourceRecord extends RcsBaseEntity<RcsBatchChangeDataSourceRecord> {

    private static final long serialVersionUID = 1L;
    /**
     * 数据库id，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 玩法Id
     */
    private Long playId;
    /**
     * 历史数据源
     */
    private String oldDataSourceCode;
    /**
     * 新数据源
     */
    private String newDataSourceCode;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * playMargainId
     */
    @TableField(exist = false)
    private Long playMargainId;
}
