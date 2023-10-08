package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  操盘操作配置表
 * @Date: 2020-03-05 16:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@NoArgsConstructor
public class RcsTradeConfig extends RcsBaseEntity<RcsTradeConfig> {
    /**
     * 表ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 赛事Id
     */
    private String matchId;
    /**
     * 级别  1赛事  2玩法  3盘口   如果是玩法阶段 id为玩法用，隔开
     */
    private Integer traderLevel;
    /**
     * 赛事id 玩法id 盘口id
     */
    private String targerData;
    /**
     * 1手动 0自动
     */
    private Integer dataSource;
    /**
     * 0开2关1封11锁
     */
    private Integer status;
    
    /**
     * 操盘平台 MTS ，PA
     */
    private String riskManagerCode;
    
    /**
     * 更新玩家
     */
    private String updateUser;

    public RcsTradeConfig(String matchId, Integer traderLevel, String targerData, Integer dataSource, Integer status, String updateUser) {
        this.matchId = matchId;
        this.traderLevel = traderLevel;
        this.targerData = targerData;
        this.dataSource = dataSource;
        this.status = status;
        this.updateUser = updateUser;
    }
}
