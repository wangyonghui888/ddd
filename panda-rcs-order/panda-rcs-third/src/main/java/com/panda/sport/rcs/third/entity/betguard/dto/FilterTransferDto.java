package com.panda.sport.rcs.third.entity.betguard.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/21 11:43
 * @description 外部请求传参
 */

@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterTransferDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //from date in filter required field.
    private Long startDateStamp;

    //to date in filter required field, the maximum date
    //range is 24 hours.
    private Long   endDateStamp;

    //Optional State filter, possible values (-1 Error, 1
    //Processed, 2 No Answer, 4 Skipped)
    private int state;

    //Optional (List of bet Ids)
    private List<Long> betIds;

    //Optional (Unique transaction Id)
    private Long DocumentId;


}
