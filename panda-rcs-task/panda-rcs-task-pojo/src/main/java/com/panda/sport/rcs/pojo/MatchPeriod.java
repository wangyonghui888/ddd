package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName MatchPeriod
 * @Description: TODO
 * @Author Vector
 * @Date 2019/11/19
 **/
@Data
public class MatchPeriod extends RcsBaseEntity<MatchPeriod> {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;

    @TableField(exist = false)
    private Long sportId;

    /**
     * 比赛阶段
     */
    private Integer period;

    /**
     * 当前比分信息
     */
    private String score;
    /**
     * 小节比分
     */
    private String setScore;
    /**
     * 阶段比分
     */
    private String periodScore;

    /**
     * 角球比分
     */
    private String cornerScore;

    private String eventCode;
    /**
     * 罚牌比分
     */
    @TableField(exist = false)
    private String cardScore;

    /**
     * 红牌比分
     */
    @TableField(exist = false)
    private String redCardScore;

    /**
     * 黄牌比分
     */
    @TableField(exist = false)
    private String yellowCardScore;


    @TableField(exist = false)
    private String yellowRedCardScore;

    /**
     * 黄牌比分
     */
    @TableField(exist = false)
    private String extraTimeScore;

    /**
     * 点球比分
     */
    private String penaltyShootout;

    /**
     * 事件发生时间. UTC时间
     */
    @TableField(exist = false)
    private Long eventTime;

    /**
     * 距离比赛开始多少秒
     */
    @TableField(exist = false)
    private Long secondsFromStart;


    private Long createTime;

    private Long modifyTime;
    /**
     * 关盘玩法的集合
     */
    @TableField(exist = false)
    private List<Long> categoryIds;
    /**
     * 网球比分
     * currentScore 小分
     * matchScore 盘分
     * setScore 局比分
     * qiangScore  抢分
     */
    @TableField(exist = false)
    private Map<String,Map<String,String>> scoreMap;

    /**
     * 局数
     */
    @TableField(exist = false)
    private Integer setNum;

    @TableField(exist = false)
    private String servesFirst;

    /**
     * 是否错误完赛事件（普通足球阶段为999才会使用该字段，0:否，1:是）
     */
    @TableField(exist = false)
    private Integer isErrorEndEvent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchPeriod that = (MatchPeriod) o;
        return Objects.equals(standardMatchId, that.standardMatchId) &&
                Objects.equals(period, that.period) &&
                Objects.equals(sportId, that.sportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(standardMatchId, score, sportId);
    }
}