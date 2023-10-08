package com.panda.sport.rcs.mgr.wrapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote;
import com.panda.sport.rcs.vo.HttpResponse;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2020-09-12 10:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaUserSingleNoteService extends IService<RcsQuotaUserSingleNote> {
    /**
     * @Description   //TODO
     * @Param [rcsQuotaUserSingleNote]
     * @Author  kimi
     * @Date   2020/9/26
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote>>
     **/
    HttpResponse<List<RcsQuotaUserSingleNote>> getList(RcsQuotaUserSingleNote  rcsQuotaUserSingleNote);

    /**
     * 初始化数据
     * @param sportId
     * @param betState
     * @return
     */
    List<RcsQuotaUserSingleNote> initRcsQuotaUserSingleNote(Integer sportId,Integer betState);

    /**
     *
     * @param sportId
     * @param betState
     * @param playId
     * @param quotaBase
     * @return
     */
    RcsQuotaUserSingleNote createRcsQuotaUserSingleNote(Integer sportId,Integer betState,Integer playId,Long quotaBase);

    List<RcsQuotaUserSingleNote> singleNoteUpdate(RcsQuotaUserSingleNote rcsQuotaUserSingleNote);

    void insertLimitLog(JSONObject data);
}
