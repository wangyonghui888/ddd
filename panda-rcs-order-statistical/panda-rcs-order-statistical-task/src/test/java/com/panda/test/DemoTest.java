package com.panda.test;

import com.panda.sport.rcs.TaskApplication;
import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.exception.SysException;
import com.panda.sport.rcs.common.vo.rule.RuleParameterVo;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileTagsRuleRelation;
import com.panda.sport.rcs.db.service.IUserProfileTagsRuleRelationService;
import com.panda.sport.rcs.job.SpecialMerchantNewUserJob;
import com.panda.sport.rcs.job.UserOrderStaticsJob;
import com.panda.sport.rcs.service.IRuleService;
import com.panda.sport.rcs.service.ITagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 单元测试
 *
 * @author lithan
 * 2020-06-30 11:27:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TaskApplication.class)
public class DemoTest {

    @Autowired
    IRuleService ruleService;
    @Autowired
    IUserProfileTagsRuleRelationService userProfileTagsRuleRelationService;

    @Autowired
    ITagService tagService;

    @Autowired
    UserOrderStaticsJob userOrderStaticsJob;

    @Test
    public void test() throws InterruptedException {
        try {
            userOrderStaticsJob.execute("1612108800000,1615651200000");
            //throw new SysException("测测");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    SpecialMerchantNewUserJob specialMerchantNewUserJob;

    @Test
    public void test2() throws Exception {
        specialMerchantNewUserJob.execute("123");
    }


}
