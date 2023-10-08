package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-01-06 15:17
 **/
@Data
public class TUserLabel extends RcsBaseEntity<TUserLabel> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户Id
     */
    private Long uid;
    /**
     * 用户一级标签
     */
    private Integer userLevel;
    /**
     * 二级标签 体育种类id  逗号隔开
     */
    private String sportList;
    /**
     * 二级标签 联赛id 逗号隔开
     */
    private String tournamentList;
    /**
     * 二级标签  玩法id 逗号隔开
     */
    private String playList;
    /**
     * 二级标签  投注类型 逗号隔开
     */
    private String orderTypeList;
    /**
     * 二级标签  投注阶段 逗号隔开
     */
    private String orderStageList;
    /**
     * 是否有效  1：是  2：否
     */
    private Integer status;
}
