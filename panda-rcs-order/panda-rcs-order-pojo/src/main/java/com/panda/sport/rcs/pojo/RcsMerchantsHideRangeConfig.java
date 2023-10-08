package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 藏单配置实体类
 * @author bobi
 */
@Data
@TableName(value = "rcs_merchants_hide_range_config")
public class RcsMerchantsHideRangeConfig implements Serializable {



        @TableId(value = "id", type = IdType.AUTO)
        private Integer id;

        /**
         * 运动种类id
         */
        private Integer sportId;

        /**
         * 藏单状态开关 0开 1关
         */
        @TableField(value = "hide_status")
        private Integer status;

        /**
         * 商户id
         */
        private Long merchantsId;
        /**
         * 商户编码
         */
        private String merchantsCode;
        /**
         * 最后编辑者
         */
        private String updateUsername;
        /**
         * 最大藏单金额
         */
        private Long hideMoney;

        /**
         * 创建时间
         */
        private Date createDt;

        /**
         * 修改时间
         */
        private Date updateTime;
}
