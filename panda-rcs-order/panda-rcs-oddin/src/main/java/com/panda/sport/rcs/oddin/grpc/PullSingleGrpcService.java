package com.panda.sport.rcs.oddin.grpc;

import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;

/**
 * @Author wiker
 * @Date 2023/6/17 18:30
 * 拉单接口
 **/
public interface PullSingleGrpcService {
    /**
     * 单个拉单和全量拉单公用一个接口,只是传递的入参不一样
     * @param dto
     * @return
     */
    Response pullSingle(TicketResultDto dto);
}
