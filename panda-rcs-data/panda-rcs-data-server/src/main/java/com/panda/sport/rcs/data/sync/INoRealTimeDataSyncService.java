package com.panda.sport.rcs.data.sync;


public interface INoRealTimeDataSyncService {
    /**
     * @MethodName:
     * @Description: 体育种类同步
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/28
    *
     * @param s*/
    int syncSportTypeData(String s) throws Exception;
    
    /**
     * @MethodName:
     * @Description: 标准体育区域同步
     * @Param: 
     * @Return: 
     * @Author: Vector
     * @Date: 2019/9/28
    *
     * @param s*/
    int syncSportRegionData(String s) throws Exception;

    /**
     * @MethodName:
     * @Description: 标准联赛表同步
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/28
    *
     * @param s*/
    int syncSportTournamentData(String s) throws Exception;

    /**
     * @MethodName:
     * @Description: 赛事表/标准球队信息/赛事球队关系表同步
     * @Param: 
     * @Return: 
     * @Author: Vector
     * @Date: 2019/9/28
    *
     * @param s*/
    int syncMathTeamData(String s) throws Exception;

    /**
     * @MethodName:
     * @Description: 玩法表/标准玩法投注项表同步
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/28
    *
     * @param s*/
    int syncSportMarketCategoryData(String s) throws Exception;

    /**
     * @MethodName:
     * @Description: 冠军同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2020/10/21
     *
     * @param s*/
    int syncOutrightMatch(String s) throws Exception ;

    /**
     * 虚拟赛事玩法同步
     * @return
     * @throws Exception
     * @param s
     */
    int queryVirtualMarketCategoryPage(String s) throws Exception ;

    /**
     * 同频系统用户
     * @return List<ShortSysUserVO>
     * @author antonio
     * @param s
     */
    int snycShortSysUserList(String s) throws Exception ;

    /**
     * 球员信息同步
     * @return
     * @throws Exception
     * @param s
     */
    int queryStandardSportPlayer(String s)throws Exception ;
}
