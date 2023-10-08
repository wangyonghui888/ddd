package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2019-11-22 16:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Data
@TableName(value = "rcs_category_odd_templet")
public class RcsCategoryOddTemplet extends RcsBaseEntity<RcsCategoryOddTemplet> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 玩法ID
     */
    private Integer category;

    private String oddType;
    /**
     * 排序
     */
    private Integer sortNo;
    /**
     * 分组
     */
    private Integer groupId;


    private String oddName;


    private String oddNameCode;

}