package com.panda.sport.rcs.pojo;


import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 标准玩法表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-07
 */

public class StandardSportMarketCategory  {

    private static final long serialVersionUID = 1L;

    public static List firstHalfMatchCategorys = Arrays.asList(17,18,19,20,21,22,23,24,29);

    public static List unCalcMatchCategorys = Arrays.asList(16,25,26,27,32,33,34);
    /**
     * 表ID, 自增
     */
    private Long id;

    /**
     * 运动种类id.  对应表 sport.id
     */
    private Long sportId;

    /**
     * 例如: total 
     */
    private String type;

    /**
     * 玩法标识.  
     */
    private String typeIdentify;

    /**
     * 激活. 激活为1, 否则为 0. 默认 1
     */
    private Integer active;

    /**
     * 玩法名称编码. 用于多语言.
     */
    private Long nameCode;

    /**
     * 玩法状态. 0已关闭; 1已创建; 2待二次校验; 3已开启; .  默认已创建
     */
    private Integer status;

    /**
     * 是否属于多盘口玩法. 0no; 1yes.  默认no
     */
    private Integer multiMarket;

    /**
     * 排序值. 
     */
    private Integer orderNo;

    /**
     * 投注项数量
     */
    private Integer fieldsNum;

    /**
     * 附件字段1
     */
    private String addition1;

    /**
     * 附件字段2
     */
    private String addition2;

    /**
     * 附件字段3
     */
    private String addition3;

    /**
     * 附件字段4
     */
    private String addition4;

    /**
     * 附件字段5
     */
    private String addition5;

    /**
     * 赔率切换system_item_dict.id列表  保存多个用逗号隔开
     */
    private String oddsSwitch;

    /**
     * 选项展示 Yes 展示 No 关闭
     */
    private String optionToShow;

    /**
     * 模板展示 
     */
    private Long templateShowing;

    /**
     * 所属时段 system_item_dict.id 
     */
    private Long theirTime;

    /**
     * 玩法构成盘口的数据格式. 例如:  Total [total] in 15 minutes interval [from]-[to] 玩法下:  当前字段的值是 from/to/total
     */
    private String dataFormate;

    /**
     * 玩法详细说明
     */
    private String description;

    /**
     * 备注.长度不超过130个字符. 
     */
    private String remark;

    /**
     * 创建时间. UTC时间, 精确到毫秒
     */
    private Long createTime;

    /**
     * 更新时间. UTC时间, 精确到毫秒
     */
    private Long modifyTime;

    private Map<String,Object> language;

    private Long relId;

    private Integer displayStyle;

    public static List getFirstHalfMatchCategorys() {
        return firstHalfMatchCategorys;
    }

    public static void setFirstHalfMatchCategorys(List firstHalfMatchCategorys) {
        StandardSportMarketCategory.firstHalfMatchCategorys = firstHalfMatchCategorys;
    }

    public static List getUnCalcMatchCategorys() {
        return unCalcMatchCategorys;
    }

    public static void setUnCalcMatchCategorys(List unCalcMatchCategorys) {
        StandardSportMarketCategory.unCalcMatchCategorys = unCalcMatchCategorys;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeIdentify() {
        return typeIdentify;
    }

    public void setTypeIdentify(String typeIdentify) {
        this.typeIdentify = typeIdentify;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMultiMarket() {
        return multiMarket;
    }

    public void setMultiMarket(Integer multiMarket) {
        this.multiMarket = multiMarket;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getFieldsNum() {
        return fieldsNum;
    }

    public void setFieldsNum(Integer fieldsNum) {
        this.fieldsNum = fieldsNum;
    }

    public String getAddition1() {
        return addition1;
    }

    public void setAddition1(String addition1) {
        this.addition1 = addition1;
    }

    public String getAddition2() {
        return addition2;
    }

    public void setAddition2(String addition2) {
        this.addition2 = addition2;
    }

    public String getAddition3() {
        return addition3;
    }

    public void setAddition3(String addition3) {
        this.addition3 = addition3;
    }

    public String getAddition4() {
        return addition4;
    }

    public void setAddition4(String addition4) {
        this.addition4 = addition4;
    }

    public String getAddition5() {
        return addition5;
    }

    public void setAddition5(String addition5) {
        this.addition5 = addition5;
    }

    public String getOddsSwitch() {
        return oddsSwitch;
    }

    public void setOddsSwitch(String oddsSwitch) {
        this.oddsSwitch = oddsSwitch;
    }

    public String getOptionToShow() {
        return optionToShow;
    }

    public void setOptionToShow(String optionToShow) {
        this.optionToShow = optionToShow;
    }

    public Long getTemplateShowing() {
        return templateShowing;
    }

    public void setTemplateShowing(Long templateShowing) {
        this.templateShowing = templateShowing;
    }

    public Long getTheirTime() {
        return theirTime;
    }

    public void setTheirTime(Long theirTime) {
        this.theirTime = theirTime;
    }

    public String getDataFormate() {
        return dataFormate;
    }

    public void setDataFormate(String dataFormate) {
        this.dataFormate = dataFormate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Map<String, Object> getLanguage() {
        return language;
    }

    public void setLanguage(Map<String, Object> language) {
        this.language = language;
    }

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

    public Integer getDisplayStyle() {
        return displayStyle;
    }

    public void setDisplayStyle(Integer displayStyle) {
        this.displayStyle = displayStyle;
    }
}
