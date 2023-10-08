package com.panda.sport.rcs.console.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelVO extends BaseRowModel {

    @ExcelProperty(index = 0)
    private String userId;

    @ExcelProperty(index = 1)
    private String sportId;

    @ExcelProperty(index = 2)
    private BigDecimal percentAge;


}