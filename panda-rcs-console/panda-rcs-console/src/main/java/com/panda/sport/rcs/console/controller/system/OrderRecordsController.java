package com.panda.sport.rcs.console.controller.system;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.panda.sport.rcs.console.common.utils.ExcelListener;
import com.panda.sport.rcs.console.dto.OrderDTO;
import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.IRcsOrderVirtualService;
import com.panda.sport.rcs.console.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title: PermissionController
 * @Description: 订单记录查询
 * @author: carver
 * @version: 1.0
 * @date: 2018/11/29 18:16
 */
@Controller
@RequestMapping("orderRecords")
@Slf4j
public class OrderRecordsController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private IRcsOrderVirtualService iRcsOrderVirtualService;

    @RequestMapping("order")
    public String order() {
        log.info("进入记录查询-订单记录");
        return "orderRecords/order";
    }

    @PostMapping("/list")
    @ResponseBody
    public PageDataResult getOrderList(@RequestParam("pageNum") Integer pageNum,
                                       @RequestParam("pageSize") Integer pageSize, OrderDTO orderDTO) {
        PageDataResult pdr = new PageDataResult();
        try {
            if (null == pageNum) pageNum = 1;
            if (null == pageSize) pageSize = 10;
            // 获取用户列表
            pdr = orderService.getOrderList(orderDTO, pageNum, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("订单记录查询异常：", e);
        }
        return pdr;
    }




    @RequestMapping("virtualOrder")
    public String orderVirtual() {
        log.info("进入记录查询-订单记录");
        return "orderRecords/virtualOrder";
    }

    @PostMapping("/orderVirtualList")
    @ResponseBody
    public PageDataResult getVirtualOrderList(@RequestParam("pageNum") Integer pageNum,
                                       @RequestParam("pageSize") Integer pageSize, OrderDTO orderDTO) {
        PageDataResult pdr = new PageDataResult();
        try {
            if (null == pageNum) pageNum = 1;
            if (null == pageSize) pageSize = 10;
            // 获取用户列表
            pdr = iRcsOrderVirtualService.getOrderList(orderDTO, pageNum, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("虚拟订单记录查询异常：", e);
        }
        return pdr;
    }



}
