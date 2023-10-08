package com.panda.sport.rcs.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 标准球员表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-12-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandardSportPlayerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表ID
     */
    private Long id;

    /**
     * 运动种类ID
     */
    private Long sportId;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 数据源编码. 对应 data_source.code
     */
    private String dataSourceCode;

    /**
     * 球员管理ID
     */
    private String playerManagerId;

    /**
     * 第三方球员id
     */
    private Long thirdPlayerId;

    /**
     * 数据商对该球员的id
     */
    private String thirdSourcePlayerId;

    /**
     * 球员照片连接地址.
     */
    private String pictureUrl;

    /**
     * 球员名称编码. 对应 language_internation.name_code
     */
    private Long nameCode;

    /**
     * 球员体重.单位: 0.01KG.
     */
    private Integer weight;

    /**
     * 球员身高, 单位: 毫米(mm)
     */
    private Integer height;

    /**
     * 球员性别. 0:未知;1:男;2:女
     */
    private Integer gender;

    /**
     * 球员出生日期.YYYY-MM-DD
     */
    private String birthday;

    /**
     * 英文名称(冗余字段,用于排序)
     */
    private String nameSpell;

    /**
     * 国籍.国籍所属国家id.对应  standard_sport_region.id
     */
    private Long countryId;

    /**
     * 个人特效. 比如: 握拍方式, 进攻特长等.
     */
    private String personalFeature;

    /**
     * 球员昵称. 例如;C罗
     */
    private String nickName;

    /**
     * 球员的中文名称, 中文简体(冗余字段,用于查询,修改是需要维护)
     */
    private String name;

    /**
     * 关联数据源编码个数
     */
    private Integer relatedDataSourceCoderNum;

    /**
     * 关联数据源编码列表.  数据样例: SR,BC,188; SR,188; BC,188
     */
    private String relatedDataSourceCoderList;

    /**
     * 全语言名称(冗余字段,用户查询)
     */
    private String allLanguageName;

    /**
     * 球员名称编码。国际化信息
     */
    private List<I18nItemDTO> il8nNameList;

    private String remark;
    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;


}
