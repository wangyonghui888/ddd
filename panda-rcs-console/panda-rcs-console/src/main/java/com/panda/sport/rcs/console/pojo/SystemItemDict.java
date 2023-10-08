package com.panda.sport.rcs.console.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Table;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.console.pojo
 * @ClassName: SystemItemDict
 * @Description: TODO
 * @Date: 2023/3/14 20:26
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
@Table(name = "system_item_dict")
public class SystemItemDict {
    private Long id;;
    private Long parentTypeId;
    private String code;
    private String value;
    private Integer active;
    private String description;
    private String addition1;
    private String remark;
    private Long createTime;
    private Long modifyTime;
}
