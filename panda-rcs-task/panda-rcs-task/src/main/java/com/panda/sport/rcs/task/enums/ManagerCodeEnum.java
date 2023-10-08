package com.panda.sport.rcs.task.enums;

/**
 * 操盘平台
 */
public enum ManagerCodeEnum {
    PA("PA", "Panda操盘平台"),
    MTS("MTS", "MTS操盘平台");

    private String id;
    private String name;

    ManagerCodeEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
