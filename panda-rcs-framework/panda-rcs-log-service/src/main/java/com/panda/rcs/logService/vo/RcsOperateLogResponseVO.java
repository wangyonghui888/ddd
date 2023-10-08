package com.panda.rcs.logService.vo;

import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class RcsOperateLogResponseVO<T> {

    private List<T> list;
    /**
     * 頁碼
     */
    private Integer pageNum;
    /**
     * 每頁數量
     */
    private Integer pageSize;
    /**
     * 總筆數
     */
    private Integer total;
}
