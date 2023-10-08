package com.panda.sport.rcs.task.wrapper.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.task.wrapper.order.TOrderDetailExtService;
import com.panda.sport.rcs.vo.OrderDetailExtVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.wrapper.impl
 * @Description :  订单明细扩展
 * @Date: 2020-01-31 11:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class TOrderDetailExtServiceImpl  extends ServiceImpl<TOrderDetailExtMapper, TOrderDetailExt> implements TOrderDetailExtService {
    @Autowired
    private TOrderDetailExtMapper mapper;
    /**
     * @Description   修改handleStatus状态
     * @Param [handleStatus, ids]
     * @Author  toney
     * @Date  14:22 2020/4/7
     * @return int
     **/
    @Override
    public int updateHandleStatusByList(Integer handleStatus, List<Long> ids){
        return mapper.updateHandleStatusByList(handleStatus, ids);
    }

    /**
     * 获取列表
     * @return
     */
    @Override
    public List<TOrderDetailExt> selectHandleOrderList(){
        return mapper.selectHandleOrderList();
    }
}
