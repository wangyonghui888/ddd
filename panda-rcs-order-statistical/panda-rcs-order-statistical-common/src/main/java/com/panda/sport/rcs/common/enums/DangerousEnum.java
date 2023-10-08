package com.panda.sport.rcs.common.enums;


/**
 * 危险投注类型枚举
 */
public enum DangerousEnum {
    SNAKE(1L, "d1"),
    //SNAKE_LIVE(2L, "d2"),
    WATER(3L, "d3"),
    INFORMATION(4L, "d4"),
    BASKETBALL(5L, "d5"),
    RISK_REJECTED(6L, "d6");

    private DangerousEnum(Long id, String dangerousCode) {
        this.id = id;
        this.dangerousCode = dangerousCode;
    }

    /**
     * id
     */
    private Long id;

    /**
     * 标识
     */
    private String dangerousCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDangerousCode() {
        return dangerousCode;
    }

    public void setDangerousCode(String dangerousCode) {
        this.dangerousCode = dangerousCode;
    }


    /**
     * 根据参数 获取字段
     *
     * @param id
     * @return
     */
    public static DangerousEnum getEnumById(String id) {
        for (DangerousEnum groupColumnEnum : DangerousEnum.values()) {
            if (groupColumnEnum.getId().equals(id)) {
                return groupColumnEnum;
            }
        }
        return null;
    }

}
