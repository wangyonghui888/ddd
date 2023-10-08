package com.panda.rcs.logService.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TUserLevel {

    private static final long serialVersionUID = 1L;

    /**
     * id. id
     */
    @TableId(value = "level_id", type = IdType.INPUT)
    private Long levelId;

    private String levelName;
}
