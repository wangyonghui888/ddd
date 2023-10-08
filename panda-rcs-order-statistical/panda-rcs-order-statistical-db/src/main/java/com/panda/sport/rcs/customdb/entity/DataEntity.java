package com.panda.sport.rcs.customdb.entity;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.entity
 * @description :   保存数据库中 数据 id  和 nameCode 对应关系的对象
 * @date: 2020-07-22 16:30
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class DataEntity {

    private Long id;

    private Long  sportId;
    
    private Long  playId;

    private Long nameCode;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataEntity{" +
                "sportId=" + sportId +
                ", playId=" + playId +
                ", nameCode=" + nameCode +
                ", name='" + name + '\'' +
                '}';
    }
}
