package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName Selection
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/21 18:44
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Selection implements Serializable {

    private static final long serialVersionUID = -5898992910939559108L;
    //盘口id
    @NotNull(message = "盘口id不能为空")
    private Long marketId;

    //赛事id
    @NotNull(message = "赛事id不能为空")
    private Long matchId;

    //投注项id
    @NotNull(message = "投注项id不能为空")
    private Long oddsId;

    //投注项赔率
    @NotNull(message = "投注项赔率不能为空")
    private Double odds;

    /**
     * 注单编号
     */
    private String betNo;

    /**
     * 赛种
     */
    Integer sportId;

    /**
     * 运动种类名称
     */
    private String sportName;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 投注项名称，直接保存业务系统传递的值
     */
    private String playOptionsName;

}
