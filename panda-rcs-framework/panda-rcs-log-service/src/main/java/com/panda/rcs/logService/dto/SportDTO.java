package com.panda.rcs.logService.dto;

import com.panda.sport.rcs.enums.SportTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * 球種-玩法DTO
 */
@Data
public class SportDTO {
    /**
     * 第三方数据源提供的该事件id
     */
    private Integer sportId;
    /**
     * 玩法集對應Map
     */
    private Map<Integer, CategorySetDTO> categorySetMap;

    public SportDTO() {
    }

    public SportDTO(SportTypeEnum sport) {
        this.sportId = sport.getCode();
    }
}
