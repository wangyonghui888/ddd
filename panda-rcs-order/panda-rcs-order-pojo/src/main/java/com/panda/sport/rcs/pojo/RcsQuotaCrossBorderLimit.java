package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-09-12 14:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsQuotaCrossBorderLimit extends RcsBaseEntity<RcsQuotaCrossBorderLimit> {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 体育种类
     */
    private Integer sportId;
    /**
     * 联赛等级
     */
    private Integer tournamentLevel;
    /**
     * 串关类型
     * 1：2串1
     * 2：3串N
     * 3：4串N
     * 4：5串N
     * 5：6串N
     * 6：7串N
     * 7：8串N 以及以上
     */
    private Integer seriesConnectionType;
    /**
     * 限额基础值
     */
    private Long quotaBase;
    /**
     * 限额比例
     */
    private BigDecimal quotaProportion;
    /**
     * 限额具体指
     */
    private BigDecimal quota;
    /**
     * 0未生效 1生效
     */
    private Integer status;

    /**
     * seriesConnectionType 转换
     *
     * @return <li>2-2串1</li>
     * <li>3-3串N</li>
     * <li>4-4串N</li>
     * <li>5-5串N</li>
     * <li>6-6串N</li>
     * <li>7-7串N</li>
     * <li>8-8串N及以上</li>
     */
    public Integer convertType() {
        if (seriesConnectionType == null) {
            return seriesConnectionType;
        }
        return seriesConnectionType + 1;
    }

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
