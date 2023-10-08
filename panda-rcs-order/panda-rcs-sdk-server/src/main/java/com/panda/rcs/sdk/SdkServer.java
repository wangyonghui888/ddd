package com.panda.rcs.sdk;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.OrderPaidApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.utils.CollectionUtil;
import com.panda.sport.rcs.utils.ListUtils;
import com.panda.sport.sdk.bean.*;
import com.panda.sport.sdk.service.impl.OrderPaidApiImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.panda.rcs.sdk.env.GuicePropertySource;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.sdk.annotation.DubboService;
import com.panda.sport.sdk.core.Sdk;
import com.panda.sport.sdk.scan.ClasspathPackageScanner;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import static com.panda.sport.sdk.bean.NacosProperitesConfig.redCatLimitConfig;

/**
 * @author lithan
 * @description
 * @date 2020/1/29 20:06
 */
@SpringBootApplication
@MapperScan(basePackages = "com.panda.sport.sdk.mapper")
@Slf4j
@ComponentScan(basePackages = {"com.panda.rcs.sdk","com.panda.sport.sdk.mapper", "com.panda.sport.sdk.mq"})
public class SdkServer {


    private static final Logger logger = LoggerFactory.getLogger(SdkServer.class);

    /**
     * pom里面配置了  会自动执行此方法
     */
    public static void main(String[] args) throws UnknownHostException {
        Properties properties = getNacosConfig();
        Map<String, String> map = new HashMap<String, String>((Map) properties);
        String port = map.get("server.port");
        System.setProperty("server.port", port);//设置服务端口
        Map<String, Map<String, String>> paramMap= new HashMap<>(1);
        paramMap.put("sdk-properties",map);
        Sdk.initProperties(paramMap);
        initDubboService();

        SpringApplication application = new SpringApplication(SdkServer.class);
        application.addInitializers(new GuicePropertySource());

        ApplicationContext context = application.run(args);
        SpringContextUtils.setContent(context);

        Environment env = context.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();
        log.info("##################################################");
        log.info("startSuccess : " + Arrays.toString(activeProfiles));
        log.info("##################################################");
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://{}:{}\n\t" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getProperty("spring.profiles.active"),

                env.getProperty("server.port")
        );
        NacosProperitesConfig.initNacosConfig(env);
        //testThirdSaveOrder();
        //testThirdMaxAmount();
    }

    public static void initDubboService() {
        //dubbo服务注册
    	ClasspathPackageScanner scan = GuiceContext.getInstance(ClasspathPackageScanner.class);
    	List<String> list = scan.getAllMatchByAnnotion(DubboService.class);
    	if(list == null || list.size() <= 0 ) return ;

    	for(String className : list) {
    		try {
    			DubboServiceConfig service = new DubboServiceConfig();
                service.setApplication(GuiceContext.getInstance(ApplicationConfig.class));
                service.setRegistry(GuiceContext.getInstance(DubboRegistryConfig.class));
                service.setProtocols(Arrays.asList(GuiceContext.getInstance(DubboProtocolConfig.class)));
                service.setTimeout(3000);
                service.setRetries(0);

                Class clazz = Class.forName(className);
                Class[] interfaces = clazz.getInterfaces();
                Class integer = null;
                for(Class inter : interfaces) {
                	if(inter.getPackage().getName().startsWith("com.panda.sport.data.rcs")) {
                		integer = inter;
                		break;
                	}
                }
                if(integer == null ) {
                	logger.warn("当前类没有实现接口，不能启动dubbo服务：{}",className);
                	continue;
                }
                service.setInterface(integer);
                service.setRef(GuiceContext.getInstance(clazz));
                service.export();

                logger.info("dubbo service export success!,name:{}",className);
    		}catch (Exception e) {
    			logger.error(e.getMessage(),e);
    		}
    	}
//    	DubboServiceConfig service = new DubboServiceConfig();
//        service.setApplication(GuiceContext.getInstance(ApplicationConfig.class));
//
//        service.setRegistry(GuiceContext.getInstance(DubboRegistryConfig.class));
//        service.setProtocols(Arrays.asList(GuiceContext.getInstance(DubboProtocolConfig.class)));
//        service.setInterface(OrderPaidApiService.class);
//        service.setRef(GuiceContext.getInstance(OrderPaidApiImpl.class));
//        service.export();
    }

    public static Properties getNacosConfig() {
        try {
            PropertiesUtil propertiesUtil =  new PropertiesUtil() ;
            String serverAddr = propertiesUtil.getValue("sdk.nacos.server.addr");
            String dataId = propertiesUtil.getValue("sdk.nacos.data.id");
            String group = propertiesUtil.getValue("sdk.nacos.group");
            String namespace = propertiesUtil.getValue("sdk.nacos.namespace");

            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);
            properties.put("namespace",namespace);

            ConfigService configService = NacosFactory.createConfigService(properties);
            String content = configService.getConfig(dataId, group, 5000);

            //转换成Properties
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            Properties nacosProperties= new Properties();
            nacosProperties.load(inputStream);
            
            return nacosProperties;

        } catch (Exception e) {
            logger.info("nacos config NacosException" + e);
            logger.error("nacos config error",e);
        }
        return null;
    }




    /**
     * 三方接口单元测试
     */
    public static void testThirdMaxAmount() {
        //测试 third
        String str = "{\"data\":{\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":0,\"items\":[{\"betAmount\":0,\"betAmount1\":0,\"dataSourceCode\":\"BE\"," +
                "\"dateExpect\":\"2023-03-26\",\"handleAfterOddsValue\":1.64,\"handleAfterOddsValue1\":1.64,\"handleStatus\":0,\"handledBetAmout\":0," +
                "\"marketId\":144923301276740598,\"marketValue\":\"-1\",\"marketValueNew\":\"-1\"," +
                "\"matchId\":3408594,\"matchProcessId\":6," +
                "\"matchType\":2,\"oddFinally\":\"1.64\",\"oddsValue\":164000.0,\"orderStatus\":0,\"otherOddsValue\":238000.0,\"paidAmount\":0.00," +
                "\"paidAmount1\":0.00,\"placeNum\":1,\"platform\":\"PA\",\"playId\":4,\"playOptions\":\"1\"," +
                "\"playOptionsId\":141532000260403245,\"scoreBenchmark\":\"1:0\"," +
                "\"sportId\":1,\"subPlayId\":\"4\",\"tournamentId\":835439,\"tradeType\":0,\"turnamentLevel\":1,\"uid\":501580967842800001," +
                "\"validateResult\":0}],\"orderStatus\":0,\"seriesType\":1,\"tenantId\":2,\"uid\":501580967842800001,\"userTagLevel\":0,\"username\":\"111111_tydVifer8rEq\"," +
                "\"validateResult\":2},\"globalId\":\"3ad3ef267cc241dc91a972e0c1bbe73d\"}";

        Request request = JSONObject.parseObject(str, Request.class);

        OrderBean orderBean = JSONObject.parseObject(request.getData().toString(), OrderBean.class);

        request.setData(orderBean);
        OrderPaidApiImpl orderPaidApiService = GuiceContext.getInstance(OrderPaidApiImpl.class);
        orderPaidApiService.queryMaxBetMoneyBySelect(request);

    }


    /**
     * 三方接口单元测试
     */
    public static void testThirdSaveOrder() {
        Long betNo=createNewOrder();
        //测试 third
        String str = "{\"data\":{\"acceptOdds\":2,\"createTime\":1679968548055,\"currencyCode\":\"CNY\",\"deviceType\":2," +
                "\"fpId\":\"a15f6c3563426827895794fc9e77f4cbd7\",\"handleStatus\":0,\"infoStatus\":0,\"ip\":\"172.21.165.120\"," +
                "\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":8400,\"betAmount1\":84," +
                "\"betNo\":\""+betNo+"\",\"betTime\":1679968548055,\"createTime\":1679968548055,\"createUser\":\"系统\"," +
                "\"dataSourceCode\":\"RC\",\"dateExpect\":\"2023-04-01\",\"handleAfterOddsValue\":4.75," +
                "\"handleAfterOddsValue1\":4.75,\"handleStatus\":0,\"handledBetAmout\":100," +
                "\"marketId\":143160341555120017,\"marketType\":\"EU\"," +
                "\"matchId\":3408053,\"matchInfo\":" +
                "\"Auto Soccer Test001xxx v Auto Soccer Test002xxx\",\"matchName\":\"桑托什杯1123\",\"matchProcessId\":0," +
                "\"matchType\":1,\"maxWinAmount\":37500.0,\"modifyTime\":1679968548055,\"modifyUser\":\"系统\"," +
                "\"oddFinally\":\"4.75\",\"oddsValue\":475000.0,\"orderNo\":\"5039905644171001\",\"orderStatus\":0," +
                "\"originOdds\":475000.0,\"otherScore\":\"\",\"paidAmount\":47500.00,\"paidAmount1\":475.00,\"placeNum\":1," +
                "\"platform\":\"PA\",\"playId\":1,\"playName\":\"全场独赢3\",\"playOptions\":\"1\"," +
                "\"playOptionsId\":143325251460794090," +
                "\"playOptionsName\":\"Auto Soccer Test001xxx\"," +
                "\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\"," +
                "\"subPlayId\":\"1\",\"tournamentId\":835439,\"tradeType\":0,\"turnamentLevel\":1," +
                "\"uid\":501580967842800001,\"validateResult\":1}],\"modifyTime\":1679968548055,\"orderAmountTotal\":8400," +
                "\"orderGroup\":\"common\",\"orderNo\":\"5039905644171001\",\"orderStatus\":0,\"productAmountTotal\":8400," +
                "\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"tenantName\":\"试玩商户\",\"uid\":501580967842800001," +
                "\"userFlag\":\"\",\"userTagLevel\":230,\"username\":\"\",\"validateResult\":1,\"vipLevel\":0}," +
                "\"globalId\":\"6d0a3130323e46908bf27c7d88a4ec5d\"}";

        Request request = JSONObject.parseObject(str, Request.class);

        OrderBean orderBean = JSONObject.parseObject(request.getData().toString(), OrderBean.class);

        request.setData(orderBean);
        OrderPaidApiImpl orderPaidApiService = GuiceContext.getInstance(OrderPaidApiImpl.class);
        orderPaidApiService.saveOrderAndValidateMaxPaid(request);

    }
    private static Long createNewOrder(){
        return System.currentTimeMillis();
    }
}
