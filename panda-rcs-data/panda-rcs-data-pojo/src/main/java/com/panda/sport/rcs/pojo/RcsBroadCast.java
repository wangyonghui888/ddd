package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :  Enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  赛事预警消息
 * @Date: 2020-09-16 16:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RcsBroadCast extends RcsBaseEntity<RcsBroadCast> {

    /**
     * 表ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息类型：1：预警消息
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
     * 扩展字段
     */
    private String extendsField;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 过期时间
     */
    private String expiresTime;

    /**
     * 创建用户
     */
    private String crtUser;

    /**
     * 接收用户
     */
    private String tagUser;

    /**
     * 1：已读 0：未读
     */
    private Integer isRead;

    /**
     * 是否有效 1:有效
     */
    private Integer status;
    /**
     * 扩展1 0早盘 1滚球
     */
    private String extendsField1;

    /**
     * 扩展字段
     */
    private String extendsField2;
}
