package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.mapper.RcsSysUserMapper;
import com.panda.sport.rcs.pojo.RcsSysUser;
import com.panda.sport.rcs.utils.TradeUserUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.RcsUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * @author :  skyKong
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  TODO
 * @Date: 2023-2-09
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsUser/")
@RefreshScope
@Api(tags = "获取风控用户信息")
public class RcsUserController {
    @Autowired
    RcsSysUserMapper rcsSysUserMapper;
    /**
     * 查询风控用户
     * */
    @PostMapping("/getRcsSysUser")
    @ApiOperation("查询风控用户")
    public HttpResponse getRcsSysUser() {
        RcsUserVo user=new RcsUserVo();
        Integer userId = 0;
        try {
            userId=TradeUserUtils.getUserId();
            RcsSysUser sysUser = rcsSysUserMapper.selectById(userId);
            user.setUserId(userId);
            user.setUserNme(sysUser.getUserCode());
        } catch (Exception e) {
            log.error("::{}::当前用户不存在，错误{}",userId,e.getMessage(),e);
            throw new RuntimeException("当前用户不存在");
        }
        return HttpResponse.success(user);
    }
}
