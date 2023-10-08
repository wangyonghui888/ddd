package com.panda.rcs.logService.dto;

import com.panda.sport.rcs.enums.FootBallCategorySetEnum;
import lombok.Data;

import java.util.List;

/**
 * 玩法集DTO
 */
@Data
public class CategorySetDTO {

    /**
     * 玩法集代碼
     */
    private String categoryCode;
    /**
     * 玩法集名稱
     */
    private String categoryName;
    /**
     * 玩法集ID
     */
    private Long categorySetId;
    /**
     * 玩法 集合
     */
    private List<Long> playIds;

    public CategorySetDTO() {
    }

    public CategorySetDTO(FootBallCategorySetEnum setEnum) {
        this.categoryCode = setEnum.name();
        this.categoryName = setEnum.getName();
        this.categorySetId = setEnum.getCategorySetId();
        this.playIds = setEnum.getCategoryIds();
    }

}
