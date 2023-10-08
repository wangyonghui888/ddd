package com.panda.sport.rcs.task.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.task.utils
 * @Description :  TODO
 * @Date: 2020-09-13 13:51
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class RcsMarketOddsConfigUtil {

    /**
     * 盘口出涨额预警的玩法
     */
    private static HashMap<Integer,List<Integer>> hashMap=new HashMap<>();
    /**
     * 足球出涨玩法的
     */
    private  static int[] playIds1=new int[]{2,4,18,19,113,114,121,122,127,128,130};

    /**
     * 网球出涨玩法的
     */
    private  static int[] playIds5=new int[]{154,155,202,160,156,157,163,164,165,169,153,161,159,204,158,162,208,166,167,168,205,206,207,170,171};

    /**
     * 斯诺克出涨玩法的
     */
    private  static int[] playIds7=new int[]{153,181,182,183,180,1,204,184,185,186,187,188,189,190,191,192,193,194,195,196,197};

    /**
     * 乒乓球出涨玩法的
     */
    private  static int[] playIds8=new int[]{153,172,173,174,175,176,177,178,179,203,204};

    /**
     * 排球出涨玩法的
     */
    private  static int[] playIds9=new int[]{153,159,204,162,172,173,253,254,255,256};

    /**
     * 篮球赔付
     */
    private  static int[] playIds2=new int[]{49,55,61,67,200,201,209,210,211,212,213,214,215,216,217,218,219,220,221};

    /**
     * 篮球赔付
     */
    private  static int[] playIds3=new int[]{242,243,244,245,246,247,248,249,250,251,252,273,274,275,276,277,278,279,280,281,282,283,284,285,286,287,288,289,290,291,292};

    /**
     * @Description  1 是出涨  2是计算赔付
     * @Param [sportId, playId]
     * @Author  kimi
     * @Date   2020/10/24
     * @return java.lang.Integer
     **/
    public static Integer getPlayIds(Integer sportId,Integer playId){
        List<Integer> list = hashMap.get(sportId);
        if (CollectionUtils.isEmpty(list)){
            init();
        }
        list=hashMap.get(sportId);
        if (sportId==1){
            if (list.contains(playId)){
                return 1;
            }else {
                return 2;
            }
        }else if (sportId==2){
            if (list.contains(playId)){
                return 2;
            }else {
                return 1;
            }
        }else if (sportId==5){
            if (list.contains(playId)){
                return 1;
            }else {
                return 2;
            }
        }else if (sportId==7){
            if (list.contains(playId)){
                return 1;
            }else {
                return 2;
            }
        }else if (sportId==8){
            if (list.contains(playId)){
                return 1;
            }else {
                return 2;
            }
        }else if (sportId==9){
            if (list.contains(playId)){
                return 1;
            }else {
                return 2;
            }
        }else if (sportId==3){
            if (list.contains(playId)){
                return 1;
            }else {
                return 2;
            }
        }
        return 2;
    }

    private static  void init(){
        List<Integer> list1 = new ArrayList<>();
        for (int x=0;x<playIds1.length;x++){
            list1.add(playIds1[x]);
        }
        hashMap.put(1,list1);

        List<Integer> list2 = new ArrayList<>();
        for (int x=0;x<playIds2.length;x++){
            list2.add(playIds2[x]);
        }
        hashMap.put(2,list2);

        List<Integer> list5 = new ArrayList<>();
        for (int x=0;x<playIds5.length;x++){
            list5.add(playIds5[x]);
        }
        hashMap.put(5,list5);

        List<Integer> list7 = new ArrayList<>();
        for (int x=0;x<playIds7.length;x++){
            list7.add(playIds7[x]);
        }
        hashMap.put(7,list7);

        List<Integer> list8 = new ArrayList<>();
        for (int x=0;x<playIds8.length;x++){
            list8.add(playIds8[x]);
        }
        hashMap.put(8,list8);

        List<Integer> list9 = new ArrayList<>();
        for (int x=0;x<playIds9.length;x++){
            list9.add(playIds9[x]);
        }
        hashMap.put(9,list9);

        List<Integer> list3 = new ArrayList<>();
        for (int x=0;x<playIds3.length;x++){
            list3.add(playIds3[x]);
        }
        hashMap.put(3,list3);
    }
}
