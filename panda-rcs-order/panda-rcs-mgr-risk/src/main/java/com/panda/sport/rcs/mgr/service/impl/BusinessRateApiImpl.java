package com.panda.sport.rcs.mgr.service.impl;


import com.panda.sport.data.rcs.api.BusinessRateApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.BusinessRateBean;
import com.panda.sport.rcs.mgr.paid.PaidService;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessRateService;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateApiDTO;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description 风控对外提供接口服务
 * 查询商户VR藏单数据
 * @Param
 * @Author max
 * @Date 11:26 2019/12/11
 * @return
 **/
@Service(connections = 5, retries = 0)
@Slf4j
@org.springframework.stereotype.Service
@Path("")
public class BusinessRateApiImpl implements BusinessRateApiService {

    @Autowired
    RcsBusinessRateService rcsBusinessRateService;

    @Override
    public Response queryBusinessRateList(Request<BusinessRateBean> requestParam) {
        List<RcsQuotaBusinessRateApiDTO> resultList = new ArrayList<>();
        try{
            if(StringUtils.isNotBlank(requestParam.getData().getBusIds())) {
                return Response.error(500, "参数不能为空");
            }
            String idsStr = requestParam.getData().getBusIds();
            String[] busIds = idsStr.split(",");
            List<String> idsList =  Arrays.asList(busIds);
            RcsQuotaBusinessRateDTO rcsQuotaBusinessRateDTO = new RcsQuotaBusinessRateDTO();
            rcsQuotaBusinessRateDTO.setBusinessIds(idsList);
            List<RcsQuotaBusinessRateDTO> list = rcsBusinessRateService.queryByBusinessIds(rcsQuotaBusinessRateDTO);
            if(list != null && list.size()>0){
                for(RcsQuotaBusinessRateDTO dto : list){
                    RcsQuotaBusinessRateApiDTO apiDTO = new RcsQuotaBusinessRateApiDTO();
                    apiDTO.setBusinessId(dto.getBusinessId());
                    apiDTO.setBusinessCode(dto.getBusinessCode());
                    apiDTO.setVrEnable(dto.getVrEnable());
                    apiDTO.setCreateTime(dto.getCreateTime());
                    apiDTO.setUpdateTime(dto.getUpdateTime());
                    resultList.add(apiDTO);
                }
            }
            return Response.success(resultList);
        } catch (Exception ex) {
            return Response.error(500, ex.getMessage());
        }
    }
}

