package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsCode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Service
public interface RcsCodeMapper extends BaseMapper<RcsCode> {

    @Select("SELECT father_key ,child_key,`value` from rcs_code WHERE father_key = 'playDisplay'")
    List<Map<String,String>> getPenSellConfig();

    List<String> selectPlayIdsByList(@Param("stateIds") List<Long> stateIds);


    /**
     * @Description   查找全部商户
     * @Param []
     * @Author  kimi
     * @Date   2020/9/29
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsCode>
     **/
    List<RcsCode> getBusinessList();

    /**
     *  更新数据
     * @param fatherKey
     * @param childKey
     * @param value
     * @param status
     */
    void insertOrUpdate(@Param("fatherKey") String fatherKey, @Param("childKey")String childKey, @Param("value")String value, @Param("status")Integer status);
    /**
     * 更新数据
     * @param childKey
     * @param value
     * @param status
     */
    void updateRcsCode( @Param("childKey")String childKey, @Param("value")String value, @Param("status")Integer status);

    /**
     * 商户同步
     * @param merchantsId
     * @param merchantsCode
     * @param status
     */
    void insertOrUpdateRcsMerchants(@Param("merchantsId")String merchantsId,@Param("merchantsCode")String merchantsCode,@Param("validStatus")Integer status, @Param("limitType")Integer limitType);

}
