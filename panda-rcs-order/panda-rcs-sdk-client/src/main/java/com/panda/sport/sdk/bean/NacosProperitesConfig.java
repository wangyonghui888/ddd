package com.panda.sport.sdk.bean;

import com.panda.sport.sdk.bean.RedCatLimitConfig;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * nacos 业务配置信息
 * @author vere
 * @date 2023-06-30
 * @version 1.0.0
 */
@Slf4j
public class NacosProperitesConfig {

    /**
     * 定义计算单位，注单是比实际多两个00
     */
    private final static int CALCULATE=100;

    private static final Logger log = LoggerFactory.getLogger(NacosProperitesConfig.class);
    /**
     * C01限额配置
     */
    public static final RedCatLimitConfig redCatLimitConfig=new RedCatLimitConfig();

    /**
     * 初始化自定义配置
     * @param environment 系统环境
     */
    public static void initNacosConfig(Environment environment){
        //1.初始化C01配置
        initRedCatLimitConfig(environment);
    }


    /**
     * 初始化C01限额配置
     * @param environment
     */
    private static void initRedCatLimitConfig(Environment environment){
        try {
            //是否开启C01
            String open= environment.getProperty("redCat.open");
            //单关单注
            String single=environment.getProperty("redcat.limit.single");
            //商户单场
            String merchant=environment.getProperty("redcat.limit.merchant");
            //用户单场
            String user=environment.getProperty("redcat.limit.user");
            //初始化C01数据源接单
            redCatLimitConfig.setOpen(StringUtils.isNotBlank(open)&&open.equals("1")?true:false);
            if (StringUtils.isNotBlank(single)) {
                String[] arrays=single.split("\\|");
                if (arrays!=null&&arrays.length>0) {
                    //初始化单关单注
                    List<RedCatSingleLimitConfig> singlelist=new ArrayList<>();
                    for (int i = 0; i < arrays.length; i++) {
                        String[] matchConfig=arrays[i].split(":");
                        if (matchConfig!=null&&matchConfig.length==2) {
                        }else{
                            log.error("C01配置信息不正确");
                            continue;
                        }
                        //默认配置信息
                        if (matchConfig[0].equals("0")) {
                            redCatLimitConfig.setDefaultSingle(Long.valueOf(matchConfig[1])*CALCULATE);
                        }else{
                            //正常配置
                            RedCatSingleLimitConfig config=new RedCatSingleLimitConfig();
                            config.setMatchLength(Integer.valueOf(matchConfig[0]));
                            config.setLimit(Long.valueOf(matchConfig[1])*CALCULATE);
                            singlelist.add(config);
                        }
                    }
                    if (!CollectionUtils.isEmpty(singlelist)) {
                        redCatLimitConfig.setSingle(singlelist);
                    }
                }
            }
            //初始化商户配置
            redCatLimitConfig.setMerchant(Long.valueOf(merchant)*CALCULATE);
            //初始化用户配置
            redCatLimitConfig.setUser(Long.valueOf(user)*CALCULATE);
        } catch (Exception ex) {
            log.error("C01配置出现异常，无法读取配置",ex);
        }

    }
}
