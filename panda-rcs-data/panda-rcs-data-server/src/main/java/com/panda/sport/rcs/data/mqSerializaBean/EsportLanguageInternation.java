package com.panda.sport.rcs.data.mqSerializaBean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 多语言
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-09-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EsportLanguageInternation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文字对应的编码
     */
    private Long nameCode;

    /**
     * 1 人工  2 系统
     * 人工不允许数据商下发的数据覆盖
     */
    private Integer flag;

    /**
     * 数据来源编码. SR BC等
     */
    private String dataSourceCode;

    /**
     * 语言类型. zh jp en 等
     */
    private String languageType;

    /**
     * 文字内容.  name_code 代表文字在 language_type代表语言下的 具体内容.比如:中国 在 英文的表示  是China. 
     */
    private String text;

    private String remark;

    private Long createTime;

    private Long modifyTime;


}
