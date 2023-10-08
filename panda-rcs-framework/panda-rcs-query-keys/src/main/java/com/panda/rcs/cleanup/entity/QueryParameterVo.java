package com.panda.rcs.cleanup.entity;

import lombok.Data;

import java.util.List;

@Data
public class QueryParameterVo {

    String keysPrefix;

    List<String> dateLists;

}
