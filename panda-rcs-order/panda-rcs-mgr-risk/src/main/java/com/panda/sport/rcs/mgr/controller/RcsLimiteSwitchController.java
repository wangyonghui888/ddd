package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.utils.CommonUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessRateService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTOReqVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  TODO
 * @Date: 2022-10-19 13:27
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsLimitSwitchConfig")
public class RcsLimiteSwitchController {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Reference(check = false, lazy = true, retries = 1, timeout = 3000)
    LimitApiService limitApiService;

    @Autowired
    RcsBusinessRateService rcsBusinessRateService;

    private static String jumpPointsConfigKey = "rsc:jump:point:config:key";

    @GetMapping("/queryLimitSwitch")
    public HttpResponse queryLimitSwitch() {
        Response<String> response = limitApiService.queryOrderLimitKeyValue();
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(response.getData())) {
            map.put("key", "0");
            map.put("amount", "2000");
            return HttpResponse.success(map);
        }
        String val = response.getData();
        map = JSON.parseObject(val, Map.class);
        return HttpResponse.success(map);

    }


    @PostMapping("/updateLimitSwitch")
    public HttpResponse updateLimitSwitch(@RequestBody Map<String, String> paramMap) {
        if (null == paramMap || paramMap.size() < 1) {
            return HttpResponse.error(-1, "要修改的参数不能为空");
        }
        sendMessage.sendMessage("rcs_order_limit_key", "", "", paramMap);
        return HttpResponse.success();
    }

    @GetMapping("/queryJumpPointsConfig")
    public HttpResponse queryJumpPointsConfig() {
        return getCommonRedisVal(jumpPointsConfigKey);
    }

    private HttpResponse getCommonRedisVal(String key) {
        String str = redisClient.get(key);
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(str)) {
            map.put("key", "0");
            map.put("amount", "2000");
            return HttpResponse.success(map);
        }
        map = JSON.parseObject(str, Map.class);
        return HttpResponse.success(map);
    }

    @PostMapping("/updateJumpPointsConfig")
    public HttpResponse updateJumpPointsConfig(@RequestBody Map<String, String> paramMap) {
        if (null == paramMap || paramMap.size() < 1) {
            return HttpResponse.error(-1, "要修改的参数不能为空");
        }
        redisClient.setExpiry(jumpPointsConfigKey, JSON.toJSONString(paramMap), 24 * 60 * 60L);
        return HttpResponse.success();

    }

    /**
     *
     * 分页查询列表
     *
     * @param:
     * @return:
     */
    @PostMapping("/listPage")
    @ResponseBody
    public HttpResponse<IPage<RcsQuotaBusinessRateDTO>> queryListPage(@RequestBody RcsQuotaBusinessRateDTOReqVo dto) {
        log.info("::{}::分页查询列表参数{}",CommonUtil.getRequestId(), dto);
        try{
            IPage<RcsQuotaBusinessRateDTO> pdr = rcsBusinessRateService.queryListPage(dto.getCurrent(),dto.getSize(),dto);
            return HttpResponse.success(pdr);
        } catch (Exception e) {
            log.error("::{}::VR查询折扣列表异常{}", CommonUtil.getRequestId(), e.getMessage(), e);
            return HttpResponse.error(HttpResponse.FAIL, e.getMessage());
        }
    }


    /**
     * 编辑折扣利率
     * @param dto
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public HttpResponse save(@RequestBody RcsQuotaBusinessRateDTO dto){
        log.info("::{}::编辑折扣利率参数{}",CommonUtil.getRequestId(), dto);
        try {
            if(dto.getBusinessId() == null || dto.getBusinessId().equals("")){
                return HttpResponse.fail("businessId传参不能为空!");
            }
            if(dto.getMtsRate() == null || dto.getMtsRate().equals("")){
                return HttpResponse.fail("mtsRate传参不能为空!");
            }
            if(dto.getVirtualRate() == null || dto.getVirtualRate().equals("")){
                return HttpResponse.fail("virtualRate传参不能为空!");
            }
            if(dto.getVrEnable() == null || dto.getVrEnable().equals("")){
                return HttpResponse.fail("vrEnable传参不能为空!");
            }
            if(dto.getCtsRate() == null || dto.getCtsRate().equals("")){
                return HttpResponse.fail("ctsRate传参不能为空!");
            }
            if(dto.getGtsRate() == null || dto.getGtsRate().equals("")){
                return HttpResponse.fail("gtsRate传参不能为空!");
            }
            if(dto.getOtsRate() == null || dto.getOtsRate().equals("")){
                return HttpResponse.fail("otsRate传参不能为空!");
            }
            if(dto.getRtsRate() == null || dto.getRtsRate().equals("")){
                return HttpResponse.fail("rtsRate传参不能为空!");
            }
            List<RcsQuotaBusinessRateDTO> list = rcsBusinessRateService.queryByBusinessId(dto);
            if(list == null || list.size()==0){
                return HttpResponse.fail("businessId参数错误!");
            }
            int result = rcsBusinessRateService.updateBusinessRate(dto,list.get(0));
            if(result>0){
                return HttpResponse.success("成功");
            }else{
                return HttpResponse.fail("失败");
            }
        } catch (Exception e) {
            log.error("::{}::保存商户折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
            return HttpResponse.fail("保存通用折扣利率异常");
        }

    }

    /**
     * 查询通用折扣利率
     * @return
     */
    @GetMapping("/getAllRate")
    @ResponseBody
    public HttpResponse getAllRate(){
        HttpResponse rs = new HttpResponse();
        try {
            return HttpResponse.success(rcsBusinessRateService.getAllRate());
        } catch (Exception e) {
            log.error("::{}::查询通用折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
            return HttpResponse.fail("查询通用折扣利率异常");
        }
    }

    /**
     * 保存通用折扣利率
     * @return
     */
    @PostMapping("/saveAllRate")
    @ResponseBody
    public HttpResponse saveAllRate(@RequestBody RcsQuotaBusinessRateDTO dto){
        log.info("::{}::保存通用折扣利率参数{}",CommonUtil.getRequestId(), dto);
        try {
            rcsBusinessRateService.saveAllRate(dto);
            return HttpResponse.success("成功");
        } catch (Exception e) {
            log.error("::{}::保存通用折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
            return HttpResponse.fail("保存通用折扣利率异常");
        }
    }

    /**
     * 批量设置
     * @param dto
     * @return
     */
    @PostMapping("/batchUpdate")
    @ResponseBody
    public HttpResponse batchUpdateBusinessRate(@RequestBody RcsQuotaBusinessRateDTO dto){
        log.info("::{}::批量设置参数{}",CommonUtil.getRequestId(), dto);
        try {
            if(dto.getBusIds() == null || dto.getBusIds().length()==0){
                return HttpResponse.fail("businessIds传参不能为空!");
            }
            rcsBusinessRateService.batchUpdateBusinessRate(dto);
            return HttpResponse.success("成功");
        } catch (Exception e) {
            log.error("::{}::批量设置异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
            return HttpResponse.fail("批量设置异常");
        }
    }

    /**
     * 虚拟批量例外设置
     * @param dto
     * @return
     */
    @PostMapping("/batchVirtualUpdate")
    @ResponseBody
    public HttpResponse batchVirtualUpdate(@RequestBody RcsQuotaBusinessRateDTO dto){
        log.info("::{}::批量设置参数{}",CommonUtil.getRequestId(), dto);
        try {
            if(dto.getBusIds() == null || dto.getBusIds().length()==0){
                return HttpResponse.fail("busIds传参不能为空!");
            }
            rcsBusinessRateService.batchUpdateVirtualRate(dto);
            return HttpResponse.success("成功");
        } catch (Exception e) {
            log.error("::{}::虚拟批量设置异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
            return HttpResponse.fail("虚拟批量设置异常");
        }
    }
    @GetMapping("/initBusinessRate")
    public void initBusinessRate(){
        rcsBusinessRateService.initBusinessRate();
    }
    @PostMapping("/initRedisBusinessRate")
    @ResponseBody
    public HttpResponse initRedisBusinessRate(){
        try {
            rcsBusinessRateService.initRedisBusinessRate();
            return HttpResponse.success("成功");
        } catch (Exception e) {
            log.error("::{}::加载redis的折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
            return HttpResponse.fail("加载redis的折扣利率异常");
//            rs.setCode("-1");
//            rs.setMessage("系统异常");
//            log.error("批量设置异常",e);
        }

    }

}
