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
 * @Date: 2020-09-04 11:11
 * @ModificationHistory Who    When    What
 * 限额其他 数据
 */
@Data
public class RcsQuotaLimitOtherData extends RcsBaseEntity<RcsQuotaLimitOtherData> {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 体育种类
     */
    private Integer sportId;
    /**
     * 1：用户单日限额（暂时没用存在用户限额表里的）
     * 2：串关 单注最低投注额
     * 3：串关单注最高投注额所占比例
     * 4：投注项计入单关限额投注比例 2串1
     * 5：投注项计入单关限额投注比例 3串1
     * 6：投注项计入单关限额投注比例 4串1
     * 7：投注项计入单关限额投注比例 5串1
     * 8：投注项计入单关限额投注比例 6串1
     * 9：投注项计入单关限额投注比例 7串1
     * 10：投注项计入单关限额投注比例 8串1
     * 11：投注项计入单关限额投注比例 9串1
     * 12：投注项计入单关限额投注比例 10串1
     * <p>
     * 103 计入串关已用额度比例3串1
     * 104 计入串关已用额度比例4串1
     * 105 计入串关已用额度比例5串1
     * 106 计入串关已用额度比例6串1
     * 107 计入串关已用额度比例7串1
     * 108 计入串关已用额度比例8串1
     * 109 计入串关已用额度比例9串1
     * 1010 计入串关已用额度比例10串1
     */
    private Integer type;
    /**
     * 值
     */
    private BigDecimal baseValue;
    /**
     * 0 未生效 1生效
     */
    private Integer status;

    /**
     * type 转换
     *
     * @return <li>2-2串1</li>
     * <li>3-3串1</li>
     * <li>4-4串1</li>
     * <li>5-5串1</li>
     * <li>6-6串1</li>
     * <li>7-7串1</li>
     * <li>8-8串1</li>
     * <li>9-9串1</li>
     * <li>10-10串1</li>
     */
    public Integer convertType() {
        if (type == null) {
            return -1;
        }
        return type - 2;
    }

    /**
     * type 转换
     *
     * @return <li>103-3串N</li>
     * <li>104-4串N</li>
     * <li>105-5串N</li>
     * <li>106-6串N</li>
     * <li>107-7串N</li>
     * <li>108-8串N</li>
     * <li>109-9串N</li>
     * <li>110-10串N</li>
     */
    public Integer convertSeriesUsedRatioType() {
        if (type == null) {
            return -1;
        }
        return type - 100;
    }

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;

}
