package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.TestMapper;
import com.panda.sport.rcs.data.service.ITestService;
import com.panda.sport.rcs.pojo.SysUser;
import org.springframework.stereotype.Service;

/**
 * ClassName: TestServicesImpl <br/>
 * Description: <br/>
 * date: 2019/9/24 21:27<br/>
 *
 * @author Administrator<br />
 * @since JDK 1.8
 */
@Service
public class TestServicesImpl extends ServiceImpl<TestMapper, SysUser> implements ITestService<SysUser> {
}
