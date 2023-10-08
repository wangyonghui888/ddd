package com.panda.rcs.order.reject.service.impl;

import com.panda.rcs.order.reject.entity.ErrorMessagePrompt;
import com.panda.rcs.order.reject.service.RejectBetService;
import com.panda.sport.data.rcs.api.CheckOddsStatusServer;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.vo.OddStatusMessagePrompt;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Service;

import java.util.Objects;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.order.reject.service.impl
 * @Description :  TODO
 * @Date: 2023-02-08 11:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RequiredArgsConstructor
@Service
public class CheckOddsStatusServerImpl implements CheckOddsStatusServer {
    private final RejectBetService rejectBetServiceImpl;
    /**
     * 检测赔率状态接口
     *
     * @param request 请求参数
     * @return 响应参数
     */
    @Override
    public Response<OddStatusMessagePrompt> checkOddsStatus(Request<OrderBean> request) {
        OddStatusMessagePrompt oddStatusMessagePrompt = new OddStatusMessagePrompt();
        if (Objects.isNull(request) || Objects.isNull(request.getData())) {
            return Response.success(oddStatusMessagePrompt);
        }
        OrderBean orderBean = request.getData();
        ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
        boolean flag = rejectBetServiceImpl.checkOddsStatus(orderBean, errorMessagePrompt);
        BeanCopyUtils.copyProperties(errorMessagePrompt, oddStatusMessagePrompt);
        oddStatusMessagePrompt.setPass(flag);
        return Response.success(oddStatusMessagePrompt);
    }
}
