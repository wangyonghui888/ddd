package com.panda.sport.rcs.core.bean;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * 风控系统所有的pojo的基类
 * @author kane
 * @since 2019-09-04
 * @version v1.1
 * @param <E>
 */
@Data
public abstract class RcsBaseEntity<E extends RcsBaseEntity> extends Model implements Serializable,Cloneable {

    /**
     * 全局唯一ID
     */
    @TableField(exist = false)
    private String globalId;

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

}
