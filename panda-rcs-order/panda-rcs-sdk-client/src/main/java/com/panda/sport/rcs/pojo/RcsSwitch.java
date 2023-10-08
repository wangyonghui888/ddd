package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author wiker
 * @date 2023/8/19 22:54
 **/
@Data
public class RcsSwitch implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    /**
     * 开关编码
     */
    private String switchCode;
    /**
     * 备注
     */

    private String remark;
    /**
     * 创建时间
     */
    private Date crtTime;
    /**
     * 修改时间
     */

    private  Timestamp updateTime;
    /**
     * 开关状态 1:开 2:关
     */

    private Integer switchStatus;
}

