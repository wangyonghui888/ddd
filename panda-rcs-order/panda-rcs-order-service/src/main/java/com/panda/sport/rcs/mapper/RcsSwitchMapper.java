package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.RcsOmitConfig;
import com.panda.sport.rcs.pojo.RcsSwitch;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import com.panda.sport.rcs.pojo.RcsSwitch;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-09-04 15:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsSwitchMapper extends BaseMapper<RcsSwitch> {

    RcsSwitch selectByCode(@Param("switchCode") String switchCode);

    int updateStatus(@Param("switchStatus") Integer switchStatus, @Param("switchCode") String switchCode);

}
