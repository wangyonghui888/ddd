package com.panda.rcs.logService.vo;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.panda.rcs.logService.utils.ExcelExport;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Z9-jing
 */
@Data
public class ExportRcsBusinessLog implements Serializable {
    @ExcelExport(value = "ID", sort = 0)
    @ColumnWidth(value = 12)
    private Integer id;

    /**
     * 操作类别
     */
    @ExcelExport(value = "操作类别", sort = 1)
    @ColumnWidth(value = 15)
    private String operateCategory;

    /**
     * 操作对象id
     */
    @ExcelExport(value = "操作对象ID", sort = 2)
    @ColumnWidth(value = 16)
    private String objectId;

    /**
     * 操作对象名称
     */
    @ExcelExport(value = "操作对象名称", sort = 3)
    @ColumnWidth(value = 16)
    private String objectName;

    /**
     * 操作对象拓展id
     */
    @ExcelExport(value = "操作对象拓展ID", sort = 4)
    @ColumnWidth(value = 20)
    private String extObjectId;

    /**
     * 操作对象拓展名称
     */
    @ExcelExport(value = "操作对象拓展名称", sort = 5)
    @ColumnWidth(value = 22)
    private String extObjectName;

    /**
     * 操作类型
     */
    @ExcelExport(value = "操作类型", sort = 6)
    @ColumnWidth(value = 15)
    private String operateType;

    /**
     * 操作参数名称
     */
    @ExcelExport(value = "操作参数名称",sort = 7)
    @ColumnWidth(value = 17)
    private String paramName;

    /**
     * 修改前参数值
     */
    @ExcelExport(value = "修改前参数值", sort = 8)
    @ColumnWidth(value = 17)
    private String beforeVal;

    /**
     * 修改后参数值
     */
    @ExcelExport(value = "修改后参数值", sort = 9)
    @ColumnWidth(value = 17)
    private String afterVal;

    /**
     * 操作人名称
     */
    @ExcelExport(value = "操作人名称", sort = 10)
    @ColumnWidth(value = 15)
    private String userName;

    /**
     * 创建时间
     */
    @ExcelExport(value = "创建时间", sort = 11)
    @ColumnWidth(value = 17)
    private String createTime;
}
