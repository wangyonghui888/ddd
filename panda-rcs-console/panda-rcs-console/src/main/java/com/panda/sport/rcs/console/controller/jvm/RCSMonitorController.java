package com.panda.sport.rcs.console.controller.jvm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.panda.sport.rcs.console.dto.JVMQueryDTO;
import com.panda.sport.rcs.console.service.RCSMonitorService;
import com.panda.sport.rcs.monitor.utils.ResultCode;

/**
 * @author tycoding
 * @date 2019-05-10
 */
@Controller
@RequestMapping("jvm")
public class RCSMonitorController {
    @Autowired
    private RCSMonitorService rCSMonitorService;

    /**
     * GC监控页
     * @return
     */
    @RequestMapping("/gc")
    public String gc() {
        return "jvm/gc";
    }

    /**
     * 内存监控页
     *
     * @return
     */
    @RequestMapping("/memory")
    public String memory() {
        return "jvm/memory";
    }

    /**
     * 线程监控页
     * @return
     */
    @RequestMapping("/systemInfo")
    public String systemInfo() {
        return "jvm/systemInfo";
    }

    /**
     * 线程监控页
     * @return
     */
    @RequestMapping("/serviceInfo")
    public String serviceInfo() {
        return "jvm/serviceInfo";
    }


    @RequestMapping("/memory/get")
    @ResponseBody
    public ResultCode getMemory(JVMQueryDTO jVMQueryDTO) {
        try {
            return new ResultCode(200, rCSMonitorService.getMemory(jVMQueryDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultCode(500, e.getMessage());
        }
    }

    @RequestMapping("serviceInfo/get")
    @ResponseBody
    public ResultCode getServiceInfo(JVMQueryDTO jVMQueryDTO) {
        try {
            return new ResultCode(200, rCSMonitorService.getServiceInfo(jVMQueryDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultCode(500, e.getMessage());
        }
    }

    @RequestMapping("systemInfo/get")
    @ResponseBody
    public ResultCode getSystemInfo(JVMQueryDTO jVMQueryDTO) {
        try {
            return new ResultCode(200, rCSMonitorService.getSystem(jVMQueryDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultCode(500, e.getMessage());
        }
    }

    @RequestMapping("/gc/get")
    @ResponseBody
    public ResultCode getGC(JVMQueryDTO jVMQueryDTO) {
        try {
            return new ResultCode(200, rCSMonitorService.getGC(jVMQueryDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultCode(500, e.getMessage());
        }
    }

    @RequestMapping("/getServerName")
    @ResponseBody
    public ResultCode getServerName(JVMQueryDTO jVMQueryDTO) {
        try {
            return new ResultCode(200, rCSMonitorService.getServerName());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultCode(500, e.getMessage());
        }
    }
    
    @RequestMapping("/thread")
    public String thread() {
        return "jvm/thread";
    }
    
    @RequestMapping("/thread/get")
    @ResponseBody
    public ResultCode threadGet(JVMQueryDTO jVMQueryDTO) {
    	return new ResultCode(200, rCSMonitorService.getThread(jVMQueryDTO));
    }
}
