package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.category.IMatrixForecast;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.scan.ClasspathPackageScanner;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.vo.CategoryVo;
import com.panda.sport.sdk.vo.OddsFieldsTemplateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  TODO
 * @Date: 2019-10-04 20:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@AutoInitMethod(init = "init")
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    /**
     * @Description 获取所有接口的实现类
     * @Param
     * @Author max
     * @Date 15:22 2019/10/9
     * @return
     **/
    private Map<String, IMatrixForecast> mapMatrix;

    public static Map<String, IMatrixForecast> matrixBeansMap = new HashMap<>();

    private static List<CategoryVo> categoryList = new ArrayList<>();

    private static List<OddsFieldsTemplateVo> oddsFieldsTemplateList = new ArrayList<>();

    public static Map<Integer, CategoryVo> categoryMap = new HashMap<>();

    public static Map<String, OddsFieldsTemplateVo> oddsFieldsTemplateMap = new HashMap<>();

    public List<CategoryVo> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryVo> categoryList) {
        CategoryService.categoryList = categoryList;
    }

    public List<OddsFieldsTemplateVo> getOddsFieldsTemplateList() {
        return oddsFieldsTemplateList;
    }

    public void setOddsFieldsTemplateList(List<OddsFieldsTemplateVo> oddsFieldsTemplateList) {
        CategoryService.oddsFieldsTemplateList = oddsFieldsTemplateList;
    }


    public Map<String, IMatrixForecast> getMapMatrixBeans() {
        return matrixBeansMap;
    }

    public void setMapMatrixBeans(Map<String, IMatrixForecast> mapMatrixBeans) {
        this.matrixBeansMap = mapMatrixBeans;
    }


    /**
     * @Description 初始化
     * @Param []
     * @Author max
     * @Date 12:44 2019/10/10
     * @return void
     **/

    /**
     * @return MatrixConstant.MatrixCategoryType
     * @Description //获取矩阵类型
     * @Param [marketCategoryId]
     * @Author max
     * @Date 21:33 2019/10/4
     **/

    public MatrixConstant.MatrixCategoryType queryMatrixCategory(Integer marketCategoryId) {
        CategoryVo categoryVo = categoryMap.get(marketCategoryId.intValue());
        if (categoryVo != null) {
            return MatrixConstant.MatrixCategoryType.values()[categoryVo.getCtype()];
        }

        return MatrixConstant.MatrixCategoryType.UNKNOWN;
    }

    /**
     * @return MatrixConstant.MatrixCategoryType
     * @Description //获取玩法对应配置数据
     * @Param [marketCategoryId]
     * @Author max
     * @Date 21:33 2019/10/4
     **/

    public CategoryVo queryCategoryVo(Integer marketCategoryId) {
        CategoryVo categoryVo = categoryMap.get(marketCategoryId.intValue());
        return categoryVo;
    }


    /**
     * @return java.lang.String
     * @Description 根据玩法ID获取Code
     * @Param [marketCategoryId]
     * @Author max
     * @Date 21:49 2019/10/4
     **/
    public String queryMatrixCodeById(Integer marketCategoryId) {
        String code = "";
        CategoryVo categoryVo = categoryMap.get(marketCategoryId.intValue());
        if (categoryVo != null) {
            code = categoryVo.getCode();
        }
        return code;
    }

    /**
     * @return java.lang.String
     * @Description 根据玩法ID和投注项Name 获取Code
     * @Param [marketCategoryId, oddsFieldsTemplate]
     * @Author max
     * @Date 21:51 2019/10/4
     **/
    public String queryMatrixTemplateCodeById(Integer marketCategoryId, String oddsFieldsTemplate) {
        String code = "";
        OddsFieldsTemplateVo vo = oddsFieldsTemplateMap.get(marketCategoryId + "," + oddsFieldsTemplate.trim().toLowerCase());
        if (vo != null) {
            code = vo.getCode();
        }

        return code;
    }

    /**
     * @return java.lang.String
     * @Description 根据玩法ID和投注项Name 获取Code
     * @Param [marketCategoryId, oddsFieldsTemplate]
     * @Author max
     * @Date 14:17 2019/10/7
     **/
    public String queryMatrixTemplateNameByCode(Integer marketCategoryId, String oddsFieldsTemplate) {
        String name = "";
        OddsFieldsTemplateVo vo = oddsFieldsTemplateMap.get(marketCategoryId + "," + oddsFieldsTemplate.trim().toLowerCase());
        if (vo != null) {
            name = vo.getName();
        }
        return name;
    }

    /**
     * @return java.lang.String
     * @Description 根据Id查询names
     * @Param [marketCategoryId]
     * @Author max
     * @Date 16:50 2019/10/7
     **/
    public String queryMatrixTemplateNameById(Long marketCategoryId) {
        String name = "";
        if (oddsFieldsTemplateList != null && categoryList != null) {
            for (int i = 0; i < oddsFieldsTemplateList.size(); i++) {
                if (oddsFieldsTemplateList.get(i).getCid() == marketCategoryId.intValue()) {
                    name += oddsFieldsTemplateList.get(i).getName() + ",";
                }
            }
        }
        return name;
    }


    public CategoryService() {
        Properties properties = new Properties();
        try {
            InputStream fileStream = CategoryService.class.getResourceAsStream("/sys.properties");
            properties.load(fileStream);

            String categoryListJson = properties.getProperty("categoryList");
            JSONArray jsonArray = JSONObject.parseArray(categoryListJson);
            categoryList = JSONArray.parseArray(jsonArray.toString(), CategoryVo.class);

            String oddsFieldsTemplateListJson = properties.getProperty("oddsFieldsTemplateList");
            jsonArray = JSONObject.parseArray(oddsFieldsTemplateListJson);
            oddsFieldsTemplateList = JSONArray.parseArray(jsonArray.toString(), OddsFieldsTemplateVo.class);

        } catch (Exception e) {
            log.info("读取categoryList/oddsFieldsTemplateList 数据异常", e);
        }

        if (categoryList != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                categoryMap.put(categoryList.get(i).getId(), categoryList.get(i));
            }
        }

        if (oddsFieldsTemplateList != null) {
            for (int i = 0; i < oddsFieldsTemplateList.size(); i++) {
                String mapKey = oddsFieldsTemplateList.get(i).getCid() + "," + oddsFieldsTemplateList.get(i).getName().trim().toLowerCase();
                mapKey = mapKey.replaceAll("'", "");
                oddsFieldsTemplateMap.put(mapKey, oddsFieldsTemplateList.get(i));
            }
        }
    }

    public void init() {
        ClasspathPackageScanner scan = GuiceContext.getInstance(ClasspathPackageScanner.class);
        List<String> list = scan.getAllMatchByInterface(IMatrixForecast.class);
        List<IMatrixForecast> castList = new ArrayList<IMatrixForecast>();
        Map<String, IMatrixForecast> mapMatrixBeans = new HashMap<>();
        for (String key : list) {
            try {
                IMatrixForecast cast = (IMatrixForecast) GuiceContext.getInstance(Class.forName(key));
                castList.add(cast);
                String queryCateCode = cast.queryCateCode();
                for (String code : queryCateCode.split(",")) {
                    /*** 将玩法id 作为key值,矩阵计算服务放入map中 ***/
                    mapMatrixBeans.put(code, cast);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        setMapMatrixBeans(mapMatrixBeans);
    }

}
