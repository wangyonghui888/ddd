package com.panda.sport.rcs.mgr.paid.matrix.bean;

import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  TODO
 * @Date: 2019-10-04 20:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@ConfigurationProperties(prefix = "category")
@Slf4j
public class CategoryList implements ApplicationContextAware {
    /**
     * @Description 获取所有接口的实现类
     * @Param
     * @Author max
     * @Date 15:22 2019/10/9
     * @return
     **/
    private Map<String, IMatrixForecast> mapMatrix;

    public Map<String, IMatrixForecast> mapMatrixBeans = new HashMap<>();
    private List<CategoryVo> categoryList = new ArrayList<>();
    private List<OddsFieldsTemplateVo> oddsFieldsTemplateList = new ArrayList<>();

    public Map<Integer, CategoryVo> map = new HashMap<>();
    public Map<String, OddsFieldsTemplateVo> mapTemplate = new HashMap<>();

    public List<CategoryVo> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryVo> categoryList) {
        this.categoryList = categoryList;
    }

    public List<OddsFieldsTemplateVo> getOddsFieldsTemplateList() {
        return oddsFieldsTemplateList;
    }

    public void setOddsFieldsTemplateList(List<OddsFieldsTemplateVo> oddsFieldsTemplateList) {
        this.oddsFieldsTemplateList = oddsFieldsTemplateList;
    }


    public Map<String, IMatrixForecast> getMapMatrixBeans() {
        return mapMatrixBeans;
    }

    public void setMapMatrixBeans(Map<String, IMatrixForecast> mapMatrixBeans) {
        this.mapMatrixBeans = mapMatrixBeans;
    }

    /**
     * @return void
     * @Description 获取所有接口的实现类
     * @Param [applicationContext]
     * @Author max
     * @Date 15:23 2019/10/9
     **/
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mapMatrix = applicationContext.getBeansOfType(IMatrixForecast.class);
        if (mapMatrix != null && mapMatrix.size() > 0) {
            for (IMatrixForecast matrixForecast : mapMatrix.values()) {

                log.info("cate_code is :" + matrixForecast.queryCateCode());
                if (matrixForecast.queryCateCode().contains(",")) {
                    String[] codes = matrixForecast.queryCateCode().split(",");
                    for (int i = 0; i < codes.length; i++) {
                        mapMatrixBeans.put(codes[i], matrixForecast);
                    }
                } else {
                    mapMatrixBeans.put(matrixForecast.queryCateCode(), matrixForecast);
                }
            }
        }
    }

    /**
     * @return void
     * @Description 初始化
     * @Param []
     * @Author max
     * @Date 12:44 2019/10/10
     **/

    @PostConstruct
    public void init() {
        if (categoryList != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                map.put(categoryList.get(i).getId(), categoryList.get(i));
            }
        }

        if (oddsFieldsTemplateList != null) {
            for (int i = 0; i < oddsFieldsTemplateList.size(); i++) {
                mapTemplate.put(oddsFieldsTemplateList.get(i).getCid() + "," + oddsFieldsTemplateList.get(i).getName().trim().toLowerCase(), oddsFieldsTemplateList.get(i));
            }
        }
    }

    /**
     * @return MatrixConstant.MatrixCategoryType
     * @Description //获取矩阵类型
     * @Param [marketCategoryId]
     * @Author max
     * @Date 21:33 2019/10/4
     **/

    public MatrixConstant.MatrixCategoryType queryMatrixCategory(Integer marketCategoryId) {
        CategoryVo categoryVo = map.get(marketCategoryId.intValue());
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
        CategoryVo categoryVo = map.get(marketCategoryId.intValue());
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
        CategoryVo categoryVo = map.get(marketCategoryId.intValue());
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
        OddsFieldsTemplateVo vo = mapTemplate.get(marketCategoryId + "," + oddsFieldsTemplate.trim().toLowerCase());
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
        OddsFieldsTemplateVo vo = mapTemplate.get(marketCategoryId + "," + oddsFieldsTemplate.trim().toLowerCase());
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
}
