package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.TUserMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@JobHandler(value = "userLinkedTableDataClearJob")
public class UserLinkedTableDataClearJob extends IJobHandler {

    private final String commMerchantCode = "oubao";
    private final String sMerchantCode = "553452";
    private final String bMerchantCode = "472028";
    private final String yMerchantCode = "105627";

    public static final List<String> commUserNameLists = new ArrayList<String>(){{
        this.add("c_tytest_");
        this.add("FC_test");
        this.add("seven_FC");
    }};

    public static final List<String> sUserNameLists = new ArrayList<String>(){{
        this.add("s_tytest_");
        this.add("FS_test");
        this.add("seven_FS");
    }};

    public static final List<String> bUserNameLists = new ArrayList<String>(){{
        this.add("b_tytest_");
        this.add("FB_test");
        this.add("seven_FB");
    }};

    public static final List<String> yUserNameLists = new ArrayList<String>(){{
        this.add("y_tytest_");
        this.add("FY_test");
        this.add("seven_FY");
        this.add("y_sixtest_");
    }};


    @Autowired
    private TUserMapper tUserMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long starTime = System.currentTimeMillis();
        //comm 组
        for (String userName : commUserNameLists){
            List<Long> userIds = tUserMapper.getUserByMerchantCodeAndTimeAndUserName(commMerchantCode, userName);
            if(userIds != null && userIds.size() > 0){

            }
        }

        //B组
        for (String userName : bUserNameLists){
            List<Long> userIds = tUserMapper.getUserByMerchantCodeAndTimeAndUserName(bMerchantCode, userName);
        }

        //S组
        for (String userName : sUserNameLists){
            List<Long> userIds = tUserMapper.getUserByMerchantCodeAndTimeAndUserName(sMerchantCode, userName);
        }

        //y组
        for (String userName : yUserNameLists){
            List<Long> userIds = tUserMapper.getUserByMerchantCodeAndTimeAndUserName(yMerchantCode, userName);
        }

        //List<Long> commUserIds
        return ReturnT.SUCCESS;
    }

}
