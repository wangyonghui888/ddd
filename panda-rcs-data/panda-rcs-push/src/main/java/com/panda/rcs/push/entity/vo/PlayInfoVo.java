package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayInfoVo implements Serializable {

    /**
     * 数据库id，自增
     */
    private Long id;

    /**
     * 运动种类id。 对应表 sport.id
     */
    private  Long sportId;

    /**
     * 玩法Id
     */
    private Long playId;

    /**
     * 玩法集名称
     */
    private String name;

}
