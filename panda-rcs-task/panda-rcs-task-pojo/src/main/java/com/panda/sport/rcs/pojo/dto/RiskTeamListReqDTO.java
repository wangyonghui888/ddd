package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class RiskTeamListReqDTO {

    private Integer page;

    private Integer pageSize;

    private List<Integer> regionIds;

    private List<Integer> sportIds;

    private Integer rows;

    private String searchType;

    private String teamLike;
}
