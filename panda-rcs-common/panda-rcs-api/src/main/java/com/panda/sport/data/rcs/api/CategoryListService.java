package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.CategoryDTO;

import java.util.List;


public interface CategoryListService  {
    /**
     * @Description  获取所有接口的实现类
     * @Param
     * @Author  holly
     * @Date  15:22 2019/10/9
     * @return
     **/
    public List<CategoryDTO> getMatrixCategoryList(Integer ctype);

    public List<CategoryDTO> getMatrixCategoryList(Integer sportId, Integer ctype);

}
