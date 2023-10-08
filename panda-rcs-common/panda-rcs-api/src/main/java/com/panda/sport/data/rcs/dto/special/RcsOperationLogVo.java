package com.panda.sport.data.rcs.dto.special;

import java.util.List;

import lombok.Data;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-02-06 15:42
 **/
@Data
public class RcsOperationLogVo {
    private List<RcsOperationLog> rcsOperationLogList;
    private Integer total;

}
