package com.panda.sport.rcs.gts.gtsenum;

public enum GtsSportsEnum {

    GTS_10("Football", "足球", 10, 1),
    GTS_4("Basketball", "篮球", 4, 2),
    GTS_3("Baseball", "棒球", 3, 3),
    GTS_15("Ice Hockey", "冰球", 15, 4),
    GTS_24("Tennis", "网球", 24, 5),
    GTS_17("American Football", "美式足球", 17, 6),
    GTS_22("Snooker", "斯诺克", 22, 7),
    GTS_269467("Table Tennis", "乒乓球", 269467, 8),
    GTS_91189("Volleyball", "排球", 91189, 9),
    GTS_271554("Badminton", "羽毛球", 271554, 10),
    GTS_99614("Handball", "手球", 99614, 11),
    GTS_5("Boxing", "拳击", 5, 12),
    GTS_7950337("Beach Volleyball", "沙滩排球", 7950337, 13),
    GTS_73744("Rugby Union", "英式橄榄球联合会", 73744, 14),
    GTS_6463041("Hurling", "爱尔兰式曲棍球", 6463041, 15),
    GTS_276032("Water Polo", "水球", 276032, 16),
    GTS_10915624("eSports", "电子竞技", 10915624, 10915624),
    GTS_2("Australian Rules", "澳式足球", 2, 222),
    GTS_11205863("3x3 Basketball ", "3*3篮球", 11205863, 11205863),
    GTS_8554("Bowls", "保龄球", 8554, 8554),
    GTS_6("Cricket", "板球", 6, 37),
    GTS_8("Darts", "飞镖",8, 38),
    GTS_491393("Futsal", "五人制足球", 491393, 491393),
    GTS_6463040("Gaelic Football", "盖尔球", 6463040, 6463040),
    GTS_208627("Hockey", "曲棍球", 208627, 208627),
    GTS_300115("Martial Arts", "武术", 300115, 300115),
    GTS_73743("Rugby League", "英式橄榄槭", 73743, 73743),
    Other("Other Sport", "其它球种", 99999, 99999);
    //gts名字
    private String gtsSprotName;

    //中文名
    private String zhSprotName;


    //gtsSportId
    private Integer gtsSportId;

    //pa SportId
    private Integer paSportId;

    private GtsSportsEnum(String gtsSprotName, String zhSprotName, Integer gtsSportId, Integer paSportId) {
        this.gtsSprotName = gtsSprotName;
        this.zhSprotName = zhSprotName;
        this.gtsSportId = gtsSportId;
        this.paSportId = paSportId;
    }

    /**
     * 根据paSportId获取枚举
     * @return
     */
    public static GtsSportsEnum getByPaSportId(Integer paSportId) {
        for (GtsSportsEnum gtsSportsEnum : GtsSportsEnum.values()) {
            if (gtsSportsEnum.getPaSportId().equals(paSportId)) {
                return gtsSportsEnum;
            }
        }
        return Other;
    }

    public String getGtsSprotName() {
        return gtsSprotName;
    }

    public void setGtsSprotName(String gtsSprotName) {
        this.gtsSprotName = gtsSprotName;
    }

    public String getZhSprotName() {
        return zhSprotName;
    }

    public void setZhSprotName(String zhSprotName) {
        this.zhSprotName = zhSprotName;
    }

    public Integer getGtsSportId() {
        return gtsSportId;
    }

    public void setGtsSportId(Integer gtsSportId) {
        this.gtsSportId = gtsSportId;
    }

    public Integer getPaSportId() {
        return paSportId;
    }

    public void setPaSportId(Integer paSportId) {
        this.paSportId = paSportId;
    }
}
