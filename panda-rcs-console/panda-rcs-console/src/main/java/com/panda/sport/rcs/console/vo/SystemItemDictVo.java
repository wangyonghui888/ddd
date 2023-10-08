package com.panda.sport.rcs.console.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.console.vo
 * @ClassName: SystemItemDictVo
 * @Description: TODO
 * @Date: 2023/3/14 21:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
public class SystemItemDictVo {
    private Long id;;
    private Long parentTypeId;
    private String code;
    private String value;
    private Integer active;
    private String description;
    private String addition1;
    private String remark;
    private Date createTimeStart;
    private Date createTimeEnd;
    private Date modifyTimeStart;
    private Date modifyTimeEnd;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
