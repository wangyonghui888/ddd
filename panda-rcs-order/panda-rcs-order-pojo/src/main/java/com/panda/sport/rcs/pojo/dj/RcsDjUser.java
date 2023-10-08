package com.panda.sport.rcs.pojo.dj;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName RcsDjUser
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/18 16:01
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsDjUser implements Serializable {


    private static final long serialVersionUID = 6000618353700133869L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    /**
     * 熊猫id
     */
    private String pandaId;

    /**
     * 电竞id
     */
    private String djId;

    /**
     * 创建时间
     */
    private Long createTime;
}
