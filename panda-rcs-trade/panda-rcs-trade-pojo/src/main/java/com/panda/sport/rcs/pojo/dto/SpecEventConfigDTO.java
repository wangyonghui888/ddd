package com.panda.sport.rcs.pojo.dto;


import lombok.Data;

/**
 * 特殊事件配置对象
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/6 15:01
 */
@Data
public class SpecEventConfigDTO {

    /**
     * 主键
     * */
    private Integer id;
    
    /**
     * 1：级别  2：联赛id   3：赛事id
     */
    private Integer type;
    
    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;

    private Integer active;
    
}
