package com.panda.sport.rcs.data.service;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface CommonService {

    String getMatchScoreSource(Long standardMatchInfoId);

    int insertRcsCategoryOddTemplet(List<Map> bean);

}
