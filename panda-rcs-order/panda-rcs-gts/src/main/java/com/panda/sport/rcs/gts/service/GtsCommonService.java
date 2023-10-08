package com.panda.sport.rcs.gts.service;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.gts.vo.ErrorMessagePrompt;
import com.panda.sport.rcs.gts.vo.GtsExtendBean;
import com.panda.sport.rcs.pojo.TOrderDetail;

import java.util.List;

/**
 * gts参数转换设置
 */
public interface GtsCommonService {

    public void convertAllParam(List<GtsExtendBean> extendBeanList);

    public void updateGtsOrder(String ticketId, String status, String orderNo, String jsonValue,
                               Integer reasonCode, String reasonMsg, Integer isCache);

    public String getTournamentTemplateValue(Long id, String type);

    public boolean dealWithData(List<TOrderDetail> tOrderDetailList, ErrorMessagePrompt errorMessagePrompt);
}
