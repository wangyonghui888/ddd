package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.RcsOmitConfig;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.RcsSysUser;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitReqVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
public interface RcsOmitConfigMapper extends BaseMapper<RcsOmitConfig> {

    /**
     * @Description 分页查询
     * @Param [page]
     * @Author  tim
     * @Date   2020/9/10
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.panda.sport.rcs.pojo.RcsOmitConfig>
     **/
    IPage<RcsOmitConfig> listPage(IPage<RcsOmitConfig> page, @Param("merchantsId") Long merchantsId, @Param("merchantsCode") String merchantsCode);

    List<RcsOmitConfig> selectByMerchantIds(@Param("merchantIds") List<Long> merchantIds);

    List<RcsOmitConfig> selectStatusOpened();

    RcsOmitConfig selectByMerchantId(@Param("merchantIds") Long merchantIds);

    int insertEntity(@Param("entity") RcsOmitConfig entity);
    int batchInsert(@Param("list") List<RcsOmitConfig> list);

    int batchInsertUpdate(@Param("list") List<RcsOmitConfig> list);

    int insertUpdate(RcsOmitConfig entity);
    int deleteAllMerchantConfig();

    int deleteAllNotInMerchantIds(@Param("list") List<Long> ids);
    int batchDeleteByMerchantId(@Param("list") List<Long> ids);
}
