package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.pojo.RcsRectanglePlay;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public interface RcsRectanglePlayService extends IService<RcsRectanglePlay> {

	int insertAndUpdate(ExtendBean extendBean);

}
