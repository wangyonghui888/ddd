package com.panda.sport.rcs.common;

/**
 * @author :  Felix
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.common
 * @Description :  TODO
 * @Date: 2019-10-10 16:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum ProducerTopicEnum {
    /**
     * 1.Margin ：玩法集margin；
     */
    Margin("panda_rcs_category_set_margin");


    private String value;

    public String getValue() {
        return value;
    }

    ProducerTopicEnum(String value) {
        this.value = value;
    }

    /**
     * 根据类型值返回类型枚举
     * @param valueParam
     * @return
     */
    public static ProducerTopicEnum getProducerTopicByValue(String valueParam) {
        for (ProducerTopicEnum producerTopicEnum : ProducerTopicEnum.values()) {
            if (producerTopicEnum.value.equals(valueParam)) {
                return producerTopicEnum;
            }
        }
        return null;
    }
}
