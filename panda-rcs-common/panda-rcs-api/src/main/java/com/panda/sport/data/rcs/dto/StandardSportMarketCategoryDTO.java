package com.panda.sport.data.rcs.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Felix
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.data.rcs.dto
 * @Description :  TODO
 * @Date: 2019-10-15 10:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardSportMarketCategoryDTO  implements Serializable {
    private Long id;

    /**
     * 运动种类id.  对应表 sport.id
     */
    private Long sportId;

    /**
     * 例如: total
     */
    private String type;

    /**
     * 玩法标识.
     */
    private String typeIdentify;

    /**
     * 激活. 激活为1, 否则为 0. 默认 1
     */
    private Integer active;

    /**
     * 玩法名称编码. 用于多语言.
     */
    private Long nameCode;

    /**
     * 玩法状态. 0已关闭; 1已创建; 2待二次校验; 3已开启; .  默认已创建
     */
    private Integer status;

    /**
     * 是否属于多盘口玩法. 0no; 1yes.  默认no
     */
    private Integer multiMarket;

    /**
     * 排序值.
     */
    private Integer orderNo;

    /**
     * 投注项数量
     */
    private Integer fieldsNum;

    /**
     * 附件字段1
     */
    private String addition1;

    /**
     * 附件字段2
     */
    private String addition2;

    /**
     * 附件字段3
     */
    private String addition3;

    /**
     * 附件字段4
     */
    private String addition4;

    /**
     * 附件字段5
     */
    private String addition5;

    /**
     * 赔率切换system_item_dict.id列表  保存多个用逗号隔开
     */
    private String oddsSwitch;

    /**
     * 选项展示 Yes 展示 No 关闭
     */
    private String optionToShow;

    /**
     * 模板展示
     */
    private Long templateShowing;

    /**
     * 所属时段 system_item_dict.id
     */
    private Long theirTime;

    /**
     * 玩法构成盘口的数据格式. 例如:  Total [total] in 15 minutes interval [from]-[to] 玩法下:  当前字段的值是 from/to/total
     */
    private String dataFormate;

    /**
     * 玩法详细说明
     */
    private String description;

    /**
     * 备注.长度不超过130个字符.
     */
    private String remark;

    /**
     * 创建时间. UTC时间, 精确到毫秒
     */
    private Long createTime;

    /**
     * 更新时间. UTC时间, 精确到毫秒
     */
    private Long modifyTime;
}
