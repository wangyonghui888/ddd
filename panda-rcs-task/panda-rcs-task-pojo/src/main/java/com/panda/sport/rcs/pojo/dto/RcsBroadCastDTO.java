package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.pojo.RcsBroadCast;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.dto
 * @Description :  TODO
 * @Date: 2020-10-24 17:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsBroadCastDTO implements Serializable{
    /**
     * 发送的消息
     */
    private RcsBroadCast rcsBroadCast;
    /**
     * 体育种类id
     */
    private Long sportId;
    /**
     * @Description
     * @Param   0早盘 1滚球
     * @Author  kimi
     * @Date   2020/10/24
     * @return 
     **/
    private Integer matchType;
    /**
     * 有权限的玩家id
     */
    private Collection<Integer> userId;
    /**
     *  消息类型
     **/
    private Integer msgType;
    /**
     *  消息标题
     **/
    private String msgTitle;
    /**
     *  赛事id
     **/
    private Integer matchId;
    /**
     *  消息内容
     **/
    private String msg;
    /**
     *  消息唯一id
     **/
    private String msgId;
    /**
     *  消息生成时间
     **/
    private String sendTime;
}
