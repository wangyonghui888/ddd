package com.panda.sport.rcs.mgr.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.mgr.utils.CommonUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessRateService;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTOReqVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author :  gulang
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-11-25 22:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
//@Controller
//@RequestMapping("/businessRate")
//@Slf4j
//public class RcsBusinessRateController {
//
//    @Autowired
//    RcsBusinessRateService rcsBusinessRateService;
//
////    @RequestMapping("/rateManage")
////    public String rateManage() {
////        return "businessRate/rateManage";
////    }
//
//    /**
//     *
//     * 分页查询列表
//     *
//     * @param:
//     * @return:
//     */
//    @PostMapping("/listPage")
//    @ResponseBody
////    @RequestMapping(value = "/listPage",method = RequestMethod.POST)
//    public HttpResponse<IPage<RcsQuotaBusinessRateDTO>> queryListPage(@RequestBody RcsQuotaBusinessRateDTOReqVo dto) {
//        try{
//            IPage<RcsQuotaBusinessRateDTO> pdr = rcsBusinessRateService.queryListPage(dto.getCurrent(),dto.getSize(),dto);
//            return HttpResponse.success(pdr);
//        } catch (Exception e) {
//            log.error("::{}::VR查询折扣列表异常{}", CommonUtil.getRequestId(), e.getMessage(), e);
//            return HttpResponse.error(HttpResponse.FAIL, e.getMessage());
//        }
//    }
//
//    /**
//     * 编辑折扣利率
//     * @param dto
//     * @return
//     */
//    @PostMapping("/save")
//    @ResponseBody
//    public HttpResponse save(@RequestBody RcsQuotaBusinessRateDTO dto){
//        try {
//            if(dto.getBusinessId() == null){
//                return HttpResponse.fail("businessId传参不能为空!");
//            }
//            List<RcsQuotaBusinessRateDTO> list = rcsBusinessRateService.queryByBusinessId(dto);
//            if(list == null || list.size()==0){
//                return HttpResponse.fail("businessId参数错误!");
//            }
//            int result = rcsBusinessRateService.updateBusinessRate(dto,list.get(0));
//            if(result>0){
//                return HttpResponse.success();
//            }else{
//                return HttpResponse.fail("失败");
//            }
//        } catch (Exception e) {
//            log.error("::{}::保存商户折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
//            return HttpResponse.fail("保存通用折扣利率异常");
//        }
//
//    }
//
//    /**
//     * 查询通用折扣利率
//     * @return
//     */
//    @GetMapping("/getAllRate")
//    @ResponseBody
//    public HttpResponse getAllRate(){
//        HttpResponse rs = new HttpResponse();
//        try {
//            return HttpResponse.success(rcsBusinessRateService.getAllRate());
//        } catch (Exception e) {
//            log.error("::{}::查询通用折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
//            return HttpResponse.fail("查询通用折扣利率异常");
//        }
//    }
//
//    /**
//     * 保存通用折扣利率
//     * @return
//     */
//    @PostMapping("/saveAllRate")
//    @ResponseBody
//    public HttpResponse saveAllRate(@RequestBody RcsQuotaBusinessRateDTO dto){
//        try {
//            rcsBusinessRateService.saveAllRate(dto);
//            return HttpResponse.success();
//        } catch (Exception e) {
//            log.error("::{}::保存通用折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
//            return HttpResponse.fail("保存通用折扣利率异常");
//        }
//    }
//
//    /**
//     * 批量设置
//     * @param dto
//     * @return
//     */
//    @PostMapping("/batchUpdate")
//    @ResponseBody
//    public HttpResponse batchUpdateBusinessRate(@RequestBody RcsQuotaBusinessRateDTO dto){
//        try {
//            if(dto.getBusIds() == null || dto.getBusIds().length()==0){
//                return HttpResponse.fail("businessIds传参不能为空!");
//            }
//            rcsBusinessRateService.batchUpdateBusinessRate(dto);
//            return HttpResponse.success();
//        } catch (Exception e) {
//            log.error("::{}::批量设置异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
//            return HttpResponse.fail("批量设置异常");
//        }
//    }
//
//    /**
//     * 虚拟批量例外设置
//     * @param dto
//     * @return
//     */
//    @PostMapping("/batchVirtualUpdate")
//    @ResponseBody
//    public HttpResponse batchVirtualUpdate(@RequestBody RcsQuotaBusinessRateDTO dto){
//        try {
//            rcsBusinessRateService.batchUpdateVirtualRate(dto);
//            return HttpResponse.success();
//        } catch (Exception e) {
//            log.error("::{}::虚拟批量设置异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
//            return HttpResponse.fail("虚拟批量设置异常");
//        }
//    }
//    @GetMapping("/initBusinessRate")
//    public void initBusinessRate(){
//        rcsBusinessRateService.initBusinessRate();
//    }
//    @PostMapping("/initRedisBusinessRate")
//    @ResponseBody
//    public HttpResponse initRedisBusinessRate(){
//        try {
//            rcsBusinessRateService.initRedisBusinessRate();
//            return HttpResponse.success();
//        } catch (Exception e) {
//            log.error("::{}::加载redis的折扣利率异常{}",CommonUtil.getRequestId(), e.getMessage(), e);
//            return HttpResponse.fail("加载redis的折扣利率异常");
////            rs.setCode("-1");
////            rs.setMessage("系统异常");
////            log.error("批量设置异常",e);
//        }
//
//    }
//}
