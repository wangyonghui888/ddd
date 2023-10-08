package com.panda.sport.rcs.pojo;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * rcs_operation_log 日志
 * @author 
 */
@Data
public class RcsOperationLog implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作编码
     */
    private String handleCode;

    private String hanlerId;

    /**
     * 修改前的内容
     */
    private String updatePreContent;

    /**
     * 修改后的内容
     */
    private String updateContent;

    /**
     * 显示的内容
     */
    private String showContent;

    private Date crtTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}