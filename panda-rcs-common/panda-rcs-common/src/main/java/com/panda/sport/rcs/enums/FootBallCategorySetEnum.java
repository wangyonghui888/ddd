package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum FootBallCategorySetEnum {

    FULL_TIME(10011L, Arrays.asList(6L,7L,12L,15L,78L,92L,141L,336L,3L,5L,28L,81L,82L,79L,80L,77L,91L,8L,9L,10L,11L,14L,68L,223L,269L,27L), "全场"),
    FIRST_HALF(10012L, Arrays.asList(70L,341L,104L,42L,43L,21L,22L,23L,103L,24L,69L,29L,30L,87L,97L,90L,100L,83L,86L,93L,96L,84L,85L,94L,95L,16L,108L,109L,110L), "上半场"),
    SECOND_HALF(10013L, Arrays.asList(25L,143L,26L,72L,342L,75L,76L,71L,73L,88L,98L,89L,99L,142L,270L), "下半场"),
    AND_OR(10014L, Arrays.asList(13L,101L,102L,105L,106L,107L), "&玩法"),
    TIME_TYEP(10015L, Arrays.asList(34L,32L,33L,31L), "时间类玩法"),
    SPECIAL_TYEP(10016L, Arrays.asList(144L,137L,35L,36L,138L,139L,140L,222L,148L,149L,150L,151L,152L), "特殊玩法"),
    CORNER_KICK(10017L, Arrays.asList(114L,122L,113L,111L,121L,119L,118L,229L,233L,225L,112L,115L,116L,117L,120L,123L,124L,125L,226L,227L,228L,230L,231L,232L,331L), "角球玩法"),
    PENALTY_CARD(10018L, Arrays.asList(307L,309L,312L,313L,224L,310L,311L,306L,308L,314L,315L,316L,317L,318L,319L,320L,321L,322L,323L,324L,325L,326L,327L,328L,329L), "罚牌"),
    EXTRA_TIME(10019L, Arrays.asList(126L,127L,128L,332L,129L,130L,343L,330L,131L,234L,235L), "加时进球"),
    PENALTY_SHOOT(10020L, Arrays.asList(333L,335L,334L,134L,240L,132L,133L,237L,238L,239L,241L), "点球"),
    PROMOTION_TYPE(10021L, Arrays.asList(135L,136L), "晋级");

    private Long categorySetId;
    private List<Long> categoryIds;
    private String name;
}