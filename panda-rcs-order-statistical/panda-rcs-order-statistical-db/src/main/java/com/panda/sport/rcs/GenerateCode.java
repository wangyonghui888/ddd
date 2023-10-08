package com.panda.sport.rcs;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs
 * @description :  TODO
 * @date: 2020-06-21 11:18
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  dorich
 * @project Name :  user_profile
 * @package Name :  PACKAGE_NAME
 * @description :  TODO
 * @date: 2020-06-19 10:26
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class GenerateCode {

    /**
     * 从数据库中生成相关的service， mapper 和 dao文件
     * @description
     * @param
     * @return void
     * @author dorich
     * @date 2020/6/19 10:30
     **/
    public void generateCode() {

        //表名，多个英文逗号分割  match_event_common,match_result_log,third_match_result,standard_match_result,

        String tableName = "t_order_detail";
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir") + "/panda-rcs-order-statistical-db";
        //String projectPath = "D:/";
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("CodeGenerator");
        gc.setOpen(false);
        gc.setSwagger2(true);
        gc.setFileOverride(true);
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setActiveRecord(true);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://172.18.178.212:6606/tybss_new?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&rewriteBatchedStatements=true");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("eIEIBhe&5rsfiD#gu1tu8FNeX!54nG@$JOL");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("db");
        pc.setParent(this.getClass().getPackage().getName());
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        //不生成 controller
        templateConfig.setController(null);
        templateConfig.setEntity("ftl/entity.java");
        templateConfig.setMapper("ftl/mapper.java");

        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        // TODO 配置表名
        strategy.setInclude(tableName.split(","));
        //strategy.setExclude(tableName.split(","));
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }


    public static void main(String[] args) {
        new  GenerateCode().generateCode();
    }
}
