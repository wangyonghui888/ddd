package com.panda.sport.rcs.pojo.tourTemplate;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;
import org.apache.tools.ant.types.resources.selectors.Exists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  联赛模板margain值
 * @Date: 2020-05-12 16:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTournamentTemplatePlayMargain implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 模板id
     */
    private Long templateId;
    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 玩法名称
     */
    @TableField(exist = false)
    private String playName;
    /**
     * 1：早盘；2：滚球
     */
    private Integer matchType;
    /**
     * 1:马来盘  2：欧洲盘
     */
    private Integer marketType;
    /**
     * 是否开售
     */
    private Integer isSell;

    /**
     * 自动关盘时间设置1、上半场期间期间2、加时上半场3、下半场结束4、下半场期间
     */
    private Integer autoCloseMarket;


    /**比赛进程时间
     *
     */
    private Long matchProgressTime;
    /**
     * 补时时间
     */
    private Long injuryTime;

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;

    private static final long serialVersionUID = 1L;


    @TableField(exist = false)
    public List<RcsTournamentTemplatePlayMargainRef> playMargainRefList;

    public List<RcsTournamentTemplatePlayMargainRef> getPlayMargainRefList() {
        return playMargainRefList;
    }
}
