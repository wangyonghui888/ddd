package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author derre
 * @date 2022-04-10
 */
//@ApiModel(value = "危险指纹池管理列表 接收 对象", description = "")
@Data
public class RiskFpListReqDTO {

    /**
     * 成功投注金额
     */
    private String betAmount;
    /**
     * 排序列 和返回字段保持一致
     */
    private String columnKey;
    /**
     * 查询开始日期
     */
    @NotBlank(message = "开始日期不能为空")
    private String startTime;
    @NotBlank(message = "结束日期不能为空")
    /**
     * 查询结束日期
     */
    private String endTime;
    /**
     * 指纹id
     */
    private String fingerprintId;
    /**
     * 平台盈利
     */
    private String netAmount;
    /**
     * 排序方式:[降序:desc, 升序:asc]
     */
    private String order;
    /**
     * 排序字段:[1:危险等级 2:关联用户 3：成功投注金额 4：平台盈利 5：平台盈利率 6：平台胜率 7:最后下注时间]
     */
    private Integer orderBy;
    /**
     * 当前第几页
     */
    private Integer page;
    /**
     * 多少条数据
     */
    private Integer pageSize;
    private Integer rows;
    /**
     * 危险等级
     */
    private Integer riskLevel;

    private Integer queryType;

}
