package com.panda;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.dj.DJBetReqVo;
import com.panda.sport.data.rcs.dto.dj.DJLimitAmoutRequest;
import com.panda.sport.data.rcs.dto.dj.DjBetOrder;
import com.panda.sport.data.rcs.dto.dj.Selection;
import com.panda.sport.rcs.DjBootstrap;
import com.panda.sport.rcs.dj.service.DjApiServiceImpl;
import com.panda.sport.rcs.dj.util.CharacterUtils;
import com.panda.sport.rcs.dj.util.HttpUtil;
import com.panda.sport.rcs.dj.util.MD5Util;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName RegisterTest
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/18 16:55
 * @Version 1.0
 **/
@SpringBootTest(classes = {DjBootstrap.class})
@RunWith(SpringRunner.class)
public class RegisterTest {

    public static final String url = "www.phiqui.com";

    public static final String path = "/v1/member/register";

    public static final String path1 = "/v1/bet/quota";


    @Test
    public void testRegister(){
        Long time = System.currentTimeMillis()/1000;
        System.out.println(time);
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("username","156689dd");
        paramMap.put("tester","1");
        paramMap.put("time",time.toString());
        paramMap.put("merchant","31433517168705439");
        paramMap.put("key","5a49502d69594196622c5b3a981b5208");

        List<String> keyList = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyList);
        StringBuilder builder = new StringBuilder();
        for (String key : keyList){
            builder.append(key).append("=").append(paramMap.get(key)).append("&");
        }
        String param = builder.toString().substring(0,builder.toString().length()-1);

        String encryt = MD5Util.stringMd5(param);

        System.out.println(encryt);
        String str1 = encryt.substring(0,9);
        String str2 = encryt.substring(9,17);
        String str3 = encryt.substring(17);

        StringBuilder builder2 = new StringBuilder(CharacterUtils.getRandomString(2));

        String sign = builder2.append(str1).append(CharacterUtils.getRandomString(2))
                .append(str2).append(CharacterUtils.getRandomString(2))
                .append(str3).append(CharacterUtils.getRandomString(2)).toString();

        System.out.println(sign);
        paramMap.put("sign",sign);
        Map<String,String> head = new HashMap<>();
        head.put("sign",sign);
        paramMap.remove("key");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for(String key: paramMap.keySet()){
            if ("key".equals(key))
                continue;
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key,paramMap.get(key));
            nameValuePairs.add(nameValuePair);
        }
        String string = HttpUtil.doPost(url,path,nameValuePairs,head);

        System.out.println(string);
    }

    @Test
    public void Test2(){
        Long time = System.currentTimeMillis()/1000;
        System.out.println(time);
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("match_id","33315665154460458");
        paramMap.put("market_id","33315702000757878");
        paramMap.put("odd_id","33315702000983449");
        paramMap.put("time",time.toString());
        paramMap.put("merchant","31433517168705439");
        paramMap.put("member_id","332073087904292197");
        paramMap.put("key","5a49502d69594196622c5b3a981b5208");



        List<String> keyList = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyList);
        StringBuilder builder = new StringBuilder();
        for (String key : keyList){
            builder.append(key).append("=").append(paramMap.get(key)).append("&");
        }
        String param = builder.toString().substring(0,builder.toString().length()-1);

        String encryt = MD5Util.stringMd5(param);

        System.out.println(encryt);
        String str1 = encryt.substring(0,9);
        String str2 = encryt.substring(9,17);
        String str3 = encryt.substring(17);

        StringBuilder builder2 = new StringBuilder(CharacterUtils.getRandomString(2));

        String sign = builder2.append(str1).append(CharacterUtils.getRandomString(2))
                .append(str2).append(CharacterUtils.getRandomString(2))
                .append(str3).append(CharacterUtils.getRandomString(2)).toString();

        System.out.println(sign);
        paramMap.put("sign",sign);
        Map<String,String> head = new HashMap<>();
        head.put("sign",sign);
        paramMap.remove("key");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for(String key: paramMap.keySet()){
            if ("key".equals(key))
                continue;
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key,paramMap.get(key));
            nameValuePairs.add(nameValuePair);
        }
        String string = HttpUtil.doGet(url,path1,nameValuePairs);
        System.out.println(string);
    }


    @Test
    public void test3(){

        DJBetReqVo djBetReqVo = new DJBetReqVo();
        djBetReqVo.setAccountName("156689ff");
        djBetReqVo.setBetNum(1);
        djBetReqVo.setBetTime(1632376915000L);
        djBetReqVo.setMerchant("31433517168705439");
        djBetReqVo.setDevice(1);
        djBetReqVo.setIp("192.168.4.100");
        djBetReqVo.setOddUpdateType(1);
        djBetReqVo.setVipLevel(0);
        djBetReqVo.setOrderNo("65464312131543");
        djBetReqVo.setUserId(88888888L);
        djBetReqVo.setSeriesType(2001);
        List<DjBetOrder> orderList = new ArrayList<>();
        DjBetOrder order = new DjBetOrder();
        order.setAmount(100L);
        order.setNum(2);
        order.setOrderType(2);
        Selection se = new Selection();
        se.setMatchId(32342751911461277L);
        se.setMarketId(32343194517523628L);
        se.setOddsId(32343194517756942L);
        se.setOdds(1.809);
        Selection selection1 = new Selection();
        selection1.setMatchId(32330818857250592L);
        selection1.setMarketId(32330827757855912L);
        selection1.setOddsId(32330827758506652L);
        selection1.setOdds(1.828);
        List<Selection> selections = Lists.newArrayList(se,selection1);
        order.setSelections(selections);
        orderList.add(order);
        djBetReqVo.setOrderList(orderList);


        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("c",djBetReqVo.getBetNum().toString());
        paramMap.put("device",djBetReqVo.getDevice().toString());
        paramMap.put("merchant",djBetReqVo.getMerchant());
        paramMap.put("uid","336956297717820274");
        paramMap.put("account",djBetReqVo.getAccountName());
        paramMap.put("ip",djBetReqVo.getIp());
        paramMap.put("odd_update_type",djBetReqVo.getOddUpdateType().toString());
        paramMap.put("account",djBetReqVo.getAccountName());
        paramMap.put("key","5a49502d69594196622c5b3a981b5208");
        if (djBetReqVo.getSeriesType()==1){
            Selection selection = djBetReqVo.getOrderList().get(0).getSelections().get(0);
            String b0 = new StringBuilder("mch=").append(selection.getMatchId())
                    .append("&mkt=").append(selection.getMarketId())
                    .append("&oid=").append(selection.getOddsId())
                    .append("&odd=").append(selection.getOdds())
                    .append("&a=").append(djBetReqVo.getOrderList().get(0).getAmount()*100)
                    .append("&bt=").append(djBetReqVo.getOrderList().get(0).getOrderType()).toString();
            paramMap.put("b[0]",b0);
        } else {
            for (int i=0 ; i< djBetReqVo.getOrderList().size(); i++){
                DjBetOrder djBetOrder = djBetReqVo.getOrderList().get(i);
                StringBuilder base = new StringBuilder("bt=").append(djBetOrder.getOrderType())
                        .append("&t=").append(djBetOrder.getNum())
                        .append("&a=").append(djBetOrder.getAmount()).append("&b=");
                for (Selection selection : djBetOrder.getSelections()){
                    base.append(selection.getMatchId())
                            .append(",").append(selection.getMarketId()).append(",")
                            .append(selection.getOddsId()).append(",").append(selection.getOdds()).append("|");
                }
                paramMap.put("b["+i+"]",base.toString().substring(0,base.toString().length()-1));
            }
        }
        List<String> keyList = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyList);
        StringBuilder builder = new StringBuilder();
        for (String key : keyList) {
            builder.append(key).append("=").append(paramMap.get(key)).append("&");
        }
        String param = builder.toString().substring(0, builder.toString().length() - 1);

        System.out.println(param);

        String encryt = MD5Util.stringMd5(param);

        System.out.println(encryt);

        String str1 = encryt.substring(0, 9);
        String str2 = encryt.substring(9, 17);
        String str3 = encryt.substring(17);

        StringBuilder builder2 = new StringBuilder(CharacterUtils.getRandomString(2));

        String sign = builder2.append(str1).append(CharacterUtils.getRandomString(2))
                .append(str2).append(CharacterUtils.getRandomString(2))
                .append(str3).append(CharacterUtils.getRandomString(2)).toString();
        paramMap.put("sign",sign);
        Map<String,String> head = new HashMap<>();
        head.put("sign",sign);
        paramMap.remove("key");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for(String key: paramMap.keySet()){
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key,paramMap.get(key));
            nameValuePairs.add(nameValuePair);
        }
        System.out.println(nameValuePairs);
        String string = HttpUtil.doPost(url,"/v1/order/bet",nameValuePairs,head);

        System.out.println(string);
    }
    @Test
    public void test4(){
        BigDecimal s=new BigDecimal(100);
        BigDecimal a=new BigDecimal(70.0000);
        String ss=s.subtract(a).toString();
        System.out.println(ss);
    }


    @Resource
    DjApiServiceImpl djApiService;
    @Test
    public void test5(){


        String str = "{\n" +
                "\t\"data\": {\n" +
                "\t\t\"merchant\": 2,\n" +
                "\t\t\"selectionList\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"marketId\": 63278359808026864,\n" +
                "\t\t\t\t\"matchId\": 63276594843107952,\n" +
                "\t\t\t\t\"matchInfo\": \"LOLbart v add\",\n" +
                "\t\t\t\t\"odds\": 4.244,\n" +
                "\t\t\t\t\"oddsId\": 63278359820997758,\n" +
                "\t\t\t\t\"playOptionsName\": \"LOLbart +3.5\",\n" +
                "\t\t\t\t\"sportId\": 100,\n" +
                "\t\t\t\t\"sportName\": \"英雄聯盟\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"marketId\": 65006878521527683,\n" +
                "\t\t\t\t\"matchId\": 65006686648517063,\n" +
                "\t\t\t\t\"matchInfo\": \"RED.A v \u2060Atletec\",\n" +
                "\t\t\t\t\"odds\": 2.0,\n" +
                "\t\t\t\t\"oddsId\": 65006878522783359,\n" +
                "\t\t\t\t\"playOptionsName\": \"Over>2.5\",\n" +
                "\t\t\t\t\"sportId\": 100,\n" +
                "\t\t\t\t\"sportName\": \"英雄聯盟\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"marketId\": 63842686689158290,\n" +
                "\t\t\t\t\"matchId\": 63842413098519310,\n" +
                "\t\t\t\t\"matchInfo\": \"AL.Y v UPAY\",\n" +
                "\t\t\t\t\"odds\": 2.0,\n" +
                "\t\t\t\t\"oddsId\": 63842686690586552,\n" +
                "\t\t\t\t\"playOptionsName\": \"AL.Y +1.5\",\n" +
                "\t\t\t\t\"sportId\": 100,\n" +
                "\t\t\t\t\"sportName\": \"英雄聯盟\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"seriesType\": 3004,\n" +
                "\t\t\"tester\": \"0\",\n" +
                "\t\t\"userId\": 504754836834900002,\n" +
                "\t\t\"username\": \"111111_ty3oX6P4eAc1\"\n" +
                "\t},\n" +
                "\t\"globalId\": \"8607f44a2b4c4f578bb0f9ab4beb59e1\"\n" +
                "}";
        Request<DJLimitAmoutRequest> request = new Request<>();

        Request request1 = JSONObject.parseObject(str, Request.class);

        djApiService.getBetAmountLimit(request1);

    }



}
