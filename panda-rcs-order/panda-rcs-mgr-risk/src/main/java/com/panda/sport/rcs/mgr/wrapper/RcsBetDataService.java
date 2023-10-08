package com.panda.sport.rcs.mgr.wrapper;

import java.util.List;

import com.panda.sport.rcs.pojo.dto.RcsRiskOrderDTO;

public interface RcsBetDataService {

    /*
     * @Description 取得'危險單關'的投注
     * @Author paul
     * @Date 2023/04/24
     */
	List<RcsRiskOrderDTO> getRiskyBet(List<String> orderNos) throws Exception;
}
