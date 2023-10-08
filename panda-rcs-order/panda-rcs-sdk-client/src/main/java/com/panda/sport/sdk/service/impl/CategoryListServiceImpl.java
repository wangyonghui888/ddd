package com.panda.sport.sdk.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.panda.sport.data.rcs.api.CategoryListService;
import com.panda.sport.data.rcs.dto.CategoryDTO;
import com.panda.sport.sdk.annotation.DubboService;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.service.impl.matrix.MatrixCaclApi;

@DubboService
public class CategoryListServiceImpl implements CategoryListService{
	
    @Inject
    MatrixAdapter matrixAdapter;

	@Override
	public List<CategoryDTO> getMatrixCategoryList(Integer ctype) {
		return getMatrixCategoryList(1, ctype);
	}

	@Override
	public List<CategoryDTO> getMatrixCategoryList(Integer sportId, Integer ctype) {
		Map<String, MatrixCaclApi> info = matrixAdapter.getHandleApiMap().get(String.valueOf(sportId));
		if(info == null) return null;
		
		List<CategoryDTO> list = new ArrayList<CategoryDTO>();
		for(String playId : info.keySet()) {
			CategoryDTO dto = new CategoryDTO();
			dto.setId(Integer.parseInt(playId));
			list.add(dto);
		}
		return list;
	}

}
