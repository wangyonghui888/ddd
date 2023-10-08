package com.panda.sport.rcs.oddin.enums;

import com.panda.sport.rcs.exeception.RcsServiceException;

import java.util.HashMap;
import java.util.Map;

/**
 * 下游数据源枚举 1：DJ 2:TY
 *
 * @author Z9-conway
 */

public enum DataSourceEnum {
    DJ(1, "DJ"),
    TY(2, "TY"),
    ;
    private Integer code;
    private String value;

    private static Map<Integer, DataSourceEnum> pool = new HashMap<Integer, DataSourceEnum>();

    static {
        for (DataSourceEnum each : DataSourceEnum.values()) {
            /**
             * todo
             * 这里永远都不会抛出异常,因为存储得时候是用code作为键存储得,然后获取到整个对象的数据
             */
            DataSourceEnum defined = pool.get(each.getValue());
            if (null != defined) {
                throw new RcsServiceException(defined.toString() + " defined as same code with "
                        + each.toString());
            }
            pool.put(each.getCode(), each);
        }
    }

    DataSourceEnum(Integer code, String value) {
        this.value = value;
        this.code = code;
    }


    public String getValue() {
        return this.value;
    }

    public Integer getCode() {
        return code;
    }

    //根据传递的SourceID得到对应枚举类中的value值(1:DJ,2:TY)
    public static String getValue(Integer val) {
        for (DataSourceEnum openState : values()) {
            if (openState.getCode().equals(val)) {
                return openState.getValue();
            }
        }
        return "";
    }
    //根据注单标识TY/DJ获取对应的枚举类中定义的Code值(SourceID)
    //(DJ:1,TY:2)
    public static Integer getCode(String value) {
        for (DataSourceEnum openState : values()) {
            if (openState.getValue().equals(value)) {
                return openState.getCode();
            }
        }
        return null;
    }

}
