package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-09-13 19:31
 * @ModificationHistory Who    When    What
 * 广播
 */
@Data
public class RcsBroadCast extends RcsBaseEntity<RcsBroadCast> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 消息类型：1：预警消息    100-105结算消息
     */
    private Integer msgType;
    /**
     * 消息id：组合键
     */
    private String msgId;
    /**
     * 广播内容
     */
    private String content;
    /**
     * 创建用户
     */
    private String crtUser;
    /**
     * 接收用户
     */
    private String tagUser;
    /**
     * 1：已读  0：未读
     */
    private Integer isRead;
    /**
     * 是否有效 1:有效
     */
    private Integer status;
    /**
     * 扩展
     */
    private String extendsField;
    /**
     * 扩展1 0早盘 1滚球
     */
    private String extendsField1;
    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 扩展2
     */
    private String extendsField2;

    @TableField(exist = false)
    private String addition1;
}
