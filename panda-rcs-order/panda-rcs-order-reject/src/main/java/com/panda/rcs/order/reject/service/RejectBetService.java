package com.panda.rcs.order.reject.service;

import com.panda.rcs.order.reject.entity.ErrorMessagePrompt;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.PreOrderRequest;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.order.reject.service.impl
 * @Description :  TODO
 * @Date: 2022-11-06 13:52
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RejectBetService {

     void contactService(OrderBean orderBean);

     void preSettleReject(PreOrderRequest orderBean);

     boolean checkOddsStatus( OrderBean orderBean,ErrorMessagePrompt errorMessagePrompt);

}
