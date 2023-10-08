package com.panda.rcs.order.entity.vo;

import lombok.Data;

/**
 * 危险数据缓存对象
 */
@Data
public class DangerVo {

    /**
     * 危险ip/危险fp（指纹）/用户id
     */
    private String objId;

    /**
     * 危险级别
     */
    private String level;

    /**
     * 创建时间
     */
    private Long createTime;

    public DangerVo(String objId, String level, Long createTime){
        this.objId = objId;
        this.level = level;
        this.createTime = createTime;
    }

}
