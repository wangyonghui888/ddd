package com.panda.rcs.logService.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.vo.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  用户
 * @Date: 2020-06-03 21:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {

   }
