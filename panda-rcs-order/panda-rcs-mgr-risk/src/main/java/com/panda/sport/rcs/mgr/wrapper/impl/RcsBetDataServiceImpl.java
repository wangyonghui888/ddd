package com.panda.sport.rcs.mgr.wrapper.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.mgr.utils.HttpUtils;
import com.panda.sport.rcs.mgr.wrapper.RcsBetDataService;
import com.panda.sport.rcs.pojo.dto.RcsRiskOrderDTO;
import com.panda.sport.rcs.vo.HttpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RcsBetDataServiceImpl implements RcsBetDataService {
    @Value("${user.portrait.risk.http.url.prefix:http://test-risk-report-web.sportxxxkd1.com/api/risk-report}")
    private String urlPrefix;

    @Value("${user.portrait.http.appId:f907e4ca9d1a11ec92e2000c29816e91}")
    private String apiId;

    @Override
    public List<RcsRiskOrderDTO> getRiskyBet(List<String> orderNos) throws Exception {
        // TODO: inject urlPrefix and replace magic string.
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("orderNo", orderNos);

        try {
            String response = HttpUtils.post(urlPrefix.concat("/riskOrder/getRiskOrderList"), JSON.toJSONString(reqParams), apiId);
            if (!StringUtils.isEmpty(response)) {
                HttpResponse<List<RcsRiskOrderDTO>> httpResponse = JSONObject.parseObject(response,
                    new TypeReference<HttpResponse<List<RcsRiskOrderDTO>>>() {});
                List<RcsRiskOrderDTO> data = httpResponse.getData();
                data.forEach(risk -> {
                    if (risk.getRiskDesc() == null)
                        risk.setRiskDesc("");
                    if (risk.getRiskType() == null)
                        risk.setRiskType("");
                });
                return data;
            }
        } catch (IOException e) {
            throw new Exception("从大数据方取得危险单关投注时出现 I/O 异常");
        }

        return null;
    }
}
