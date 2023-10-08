package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2020-12-03 17:46
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class BasketballVo extends RcsBaseEntity<BasketballVo> {
    HashMap<Object,Object> hashMap=new HashMap<>();
}
