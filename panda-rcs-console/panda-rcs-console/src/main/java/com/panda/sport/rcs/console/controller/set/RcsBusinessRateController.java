package com.panda.sport.rcs.console.controller.set;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.console.common.utils.ExcelListener;
import com.panda.sport.rcs.console.dto.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.console.pojo.RcsQuotaBusinessRateExcelVO;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.response.ResponseResult;
import com.panda.sport.rcs.console.service.RcsBusinessRateService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.console.common.Constants.DJ_OTS_AMOUNT_RATE;

@Controller
@RequestMapping("/businessRate")
@Slf4j
public class RcsBusinessRateController {

    @Autowired
    RcsBusinessRateService rcsBusinessRateService;
    @Autowired
    private RedisClient redisClient;

    @RequestMapping("/rateManage")
    public String rateManage() {
        return "businessRate/rateManage";
    }

    @RequestMapping("/djRateManage")
    public String djRateManage() {
        return "businessRate/djRateManage";
    }

    /**
     * 分页查询列表
     *
     * @param:
     * @return:
     */
    @PostMapping("/listPage")
    @ResponseBody
    public PageDataResult getUserList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                      RcsQuotaBusinessRateDTO dto) {
        PageDataResult pdr = rcsBusinessRateService.listPage(pageNum, pageSize, dto);
        return pdr;
    }

    /**
     * 编辑折扣利率
     *
     * @param dto
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public ResponseResult save(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.updateBusinessRate(dto);
            rs.setCode("1");
            rs.setMessage("保存成功");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("商户折扣利率异常", e);
        }
        return rs;
    }

    /**
     * 查询通用折扣利率
     *
     * @return
     */
    @GetMapping("/getAllRate")
    @ResponseBody
    public ResponseResult getAllRate() {
        ResponseResult rs = new ResponseResult();
        try {
            rs.setObj(rcsBusinessRateService.getAllRate());
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("查询通用折扣利率异常", e);
        }
        return rs;
    }

    /**
     * 保存通用折扣利率
     *
     * @return
     */
    @PostMapping("/saveAllRate")
    @ResponseBody
    public ResponseResult saveAllRate(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.saveAllRate(dto);
            rs.setMessage("保存成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("保存通用折扣利率异常", e);
        }
        return rs;
    }

    /**
     * 批量设置
     *
     * @param dto
     * @return
     */
    @PostMapping("/batchUpdate")
    @ResponseBody
    public ResponseResult batchUpdateBusinessRate(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.batchUpdateBusinessRate(dto);
            rs.setMessage("保存成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("批量设置异常", e);
        }
        return rs;
    }

    /**
     * 虚拟批量设置
     *
     * @param dto
     * @return
     */
    @PostMapping("/batchVirtualUpdate")
    @ResponseBody
    public ResponseResult batchVirtualUpdate(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.batchUpdateVirtualRate(dto);
            rs.setMessage("保存成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("批量设置异常", e);
        }
        return rs;
    }

    @GetMapping("/initBusinessRate")
    public void initBusinessRate() {
        rcsBusinessRateService.initBusinessRate();
        ;
    }

    @PostMapping("/initRedisBusinessRate")
    @ResponseBody
    public ResponseResult initRedisBusinessRate() {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.initRedisBusinessRate();
            ;
            rs.setMessage("操作成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("批量设置异常", e);
        }
        return rs;

    }

    /**
     * 分页查询列表
     *
     * @param:
     * @return:
     */
    @PostMapping("/listPageDj")
    @ResponseBody
    public PageDataResult getUserListDj(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                        RcsQuotaBusinessRateDTO dto) {
        PageDataResult pdr = rcsBusinessRateService.listPageDj(pageNum, pageSize, dto);
        return pdr;
    }

    /**
     * 编辑折扣利率
     *
     * @param dto
     * @return
     */
    @PostMapping("/saveDj")
    @ResponseBody
    public ResponseResult saveDj(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.updateBusinessRateDj(dto);
            rs.setCode("1");
            rs.setMessage("保存成功");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("商户折扣利率异常", e);
        }
        return rs;
    }

    /**
     * 查询通用折扣利率
     *
     * @return
     */
    @GetMapping("/getAllRateDj")
    @ResponseBody
    public ResponseResult getAllRateDj() {
        ResponseResult rs = new ResponseResult();
        try {
            rs.setObj(rcsBusinessRateService.getAllRateDj());
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("查询通用折扣利率异常", e);
        }
        return rs;
    }

    /**
     * 保存通用折扣利率
     *
     * @return
     */
    @PostMapping("/saveAllRateDj")
    @ResponseBody
    public ResponseResult saveAllRateDj(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.saveAllRateDj(dto);
            rs.setMessage("保存成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("保存通用折扣利率异常", e);
        }
        return rs;
    }

    /**
     * 批量设置
     *
     * @param dto
     * @return
     */
    @PostMapping("/batchUpdateDj")
    @ResponseBody
    public ResponseResult batchUpdateBusinessRateDj(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.batchUpdateBusinessRateDj(dto);
            rs.setMessage("保存成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("批量设置异常", e);
        }
        return rs;
    }

    /**
     * 批量设置
     *
     * @param dto
     * @return
     */
    @PostMapping("/batchAddDj")
    @ResponseBody
    public ResponseResult batchAddBusinessRateDj(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.batchAddBusinessRateDj(dto);
            rs.setMessage("保存成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("批量设置异常", e);
        }
        return rs;
    }

    /**
     * 虚拟批量设置
     *
     * @param dto
     * @return
     */
    @PostMapping("/batchVirtualUpdateDj")
    @ResponseBody
    public ResponseResult batchVirtualUpdateDj(RcsQuotaBusinessRateDTO dto) {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.batchUpdateVirtualRateDj(dto);
            rs.setMessage("保存成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("批量设置异常", e);
        }
        return rs;
    }

    @GetMapping("/initBusinessRateDj")
    public void initBusinessRateDj() {
        rcsBusinessRateService.initBusinessRateDj();
        ;
    }

    @PostMapping("/initRedisBusinessRateDj")
    @ResponseBody
    public ResponseResult initRedisBusinessRateDj() {
        ResponseResult rs = new ResponseResult();
        try {
            rcsBusinessRateService.initRedisBusinessRateDj();
            ;
            rs.setMessage("操作成功");
            rs.setCode("1");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("系统异常");
            log.error("批量设置异常", e);
        }
        return rs;

    }

    @PostMapping("/importExcel")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        long startOrigin = System.currentTimeMillis();
        String linkId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put("linkId", linkId);
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (!".xls".equalsIgnoreCase(suffix) && !".xlsx".equalsIgnoreCase(suffix)) {
            log.error("::{}::动态藏单比例数据导入异常-excel格式不正确", linkId);
            MDC.remove("linkId");
            return "/error";
        }
        log.info("::{}::动态藏单比例数据excle导入开始", linkId);
        ExcelListener listener = new ExcelListener();
        List<RcsQuotaBusinessRateExcelVO> collect = new ArrayList<>();
        InputStream inputStream = null;
        ExcelReader excelReader = null;
        try {
            inputStream = file.getInputStream();
            excelReader = new ExcelReader(inputStream, suffix.equals("xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX, null, listener);
            excelReader.read(new Sheet(1, 1, RcsQuotaBusinessRateExcelVO.class));
            List<Object> list = listener.getDatas();
            if (list.size() > 1) {
                collect = list.stream().map(m -> (RcsQuotaBusinessRateExcelVO) m).collect(Collectors.toList());
                listener.clearDatas();
            }
            if (!CollectionUtils.isEmpty(collect)) {
//                Iterator<RcsQuotaBusinessRateDTO> iterators = collect.iterator();
//                while (iterators.hasNext()) {
//                    RcsQuotaBusinessRateDTO vo = iterators.next();
//                    String userTag = redisClient.hGet("risk:trade:rcs_limit_tag", vo.getUserId());
//                    if ("115".equals(userTag) || "116".equals(userTag)) {
//                        iterators.remove();
//                    }
//                }
                List<List<RcsQuotaBusinessRateExcelVO>> lists = Lists.partition(collect, 20000);

                CountDownLatch countDownLatch = new CountDownLatch(lists.size());
                log.info("::{}::动态藏单比例数据入库开始，处理线程数:{}", linkId, lists.size());
                long start = System.currentTimeMillis();
                for (List<RcsQuotaBusinessRateExcelVO> listSub : lists) {
                    rcsBusinessRateService.batchAddOrUpdateBusinessRateDj(listSub, countDownLatch);
                }
                try {
                    countDownLatch.await(); //保证之前的所有的线程都执行完成，才会走下面的；
                } catch (Exception e) {
                    log.error("::{}::动态藏单比例数据入库阻塞异常:{}", linkId, e.getMessage());
                }
                long end = System.currentTimeMillis();
                log.info("::{}::动态藏单比例数据入库结束，耗时:{}.开始异步刷新缓存", linkId, end - start);
                final Map<String, List<RcsQuotaBusinessRateExcelVO>> excelVOGroupByUserIdList =
                        collect.stream().collect(Collectors.groupingBy(RcsQuotaBusinessRateExcelVO::getBusinessId));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //更新缓存
                        String key = DJ_OTS_AMOUNT_RATE;
                        excelVOGroupByUserIdList.forEach((k, v) -> {
                            String betFormat = String.format(key, k);
                            for (RcsQuotaBusinessRateExcelVO vo : v) {
                                redisClient.hSet(betFormat, String.valueOf(vo.getBusinessId()), String.valueOf(vo.getOtsRate()));
                            }
                        });
                    }
                }).start();
            }
        } catch (Exception e) {
            log.error("::{}::动态藏单比例数据excle导入异常:{}", linkId, e.getMessage());
            return "/error";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ex) {
                    log.error("::{}::动态藏单比例数据excle导入,关闭输入流异常:{}", linkId, ex.getMessage());
                }
            }
            if (excelReader != null) {
                excelReader.finish();
            }
            MDC.remove("linkId");
        }
        long endOrigin = System.currentTimeMillis();
        log.info("::{}::动态藏单比例数据excle导入成功,总计耗时：{}", linkId, endOrigin - startOrigin);
        return djRateManage();
    }

}
