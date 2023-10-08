package com.panda.sport.rcs.console.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @Description //玩法配置表
 * @Param
 * @Author kimi
 * @Date 2020/1/15
 * @return
 **/
@Data
@Table(name = "rcs_match_play_config_logs")
public class RcsMatchPlayConfigLogs {
    /**
     * 主键
     */
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 玩法阶段id
     */
    private Integer playId;
    /**
     * 玩法状态  0开1封
     */
    private Integer status;
    /**
     * 自动或者手动
     * 是否使用数据源0：手动；1：自动
     */
    private Integer dataSource;
}
