package com.panda.sport.rcs;

import com.panda.sport.rcs.mgr.RiskBootstrap;
import com.panda.sport.rcs.mgr.mq.bean.HideOrderDTO;
import com.panda.sport.rcs.mgr.service.orderhide.ITOrderHideService;
import com.panda.sport.rcs.mgr.service.orderhide.impl.RcsOrderHideServiceImpl;
import com.panda.sport.rcs.mgr.wrapper.RcsBetDataService;
import com.panda.sport.rcs.pojo.AmountTypeVo;
import com.panda.sport.rcs.pojo.TOrderHidePO;
import com.panda.sport.rcs.pojo.enums.OrderHideCategoryEnum;
import com.panda.sport.rcs.pojo.enums.SpecialEnum;
import com.panda.sport.rcs.service.dal.TOrderHideDal;
import com.panda.sport.rcs.vo.TOrderHide;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs
 * @Description :  TODO
 * @Date: 2020-01-20 18:51
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RiskBootstrap.class)
@Slf4j
public class OrderHideTest {

    @Autowired
    private ITOrderHideService itOrderHideService;

    @Autowired
    private RcsOrderHideServiceImpl rcsOrderHideService;

    @Autowired
    private TOrderHideDal orderHideDal;
    
    @Autowired
    private RcsBetDataService rcsBetDataService;
    
    @Test
    public void getRiskyBetTest() throws Exception{
        String orderNo="3296649746708097";
        List<String> list = new ArrayList<String>();
        list.add("3296649746708097");
        list.add("3296749714830565");
        rcsBetDataService.getRiskyBet(list);
    }

//    @Test
    public void userLabelTest(){
        String orderNo="49565687188234303421";
        AmountTypeVo amountTypeVo=new AmountTypeVo();
        amountTypeVo.setCategory(OrderHideCategoryEnum.LABEL.getId());
        amountTypeVo.setSpecial(SpecialEnum.Special.getId());
        amountTypeVo.setVolumePercentage(new BigDecimal(0.5));
        HideOrderDTO hideOrderDTO=new HideOrderDTO(amountTypeVo,orderNo,1,amountTypeVo.getVolumePercentage(),0);
        rcsOrderHideService.doHideOrderHand(hideOrderDTO);
        System.out.println("userLabelTest");

    }
//    @Test
    public void specialMerchantTest12(){
        String orderNo="49565687188234302122";
        AmountTypeVo amountTypeVo=new AmountTypeVo();
        amountTypeVo.setCategory(OrderHideCategoryEnum.MERCHANT.getId());
        amountTypeVo.setSpecial(SpecialEnum.ordinary.getId());
        amountTypeVo.setVolumePercentage(new BigDecimal(0.5));
        HideOrderDTO hideOrderDTO=new HideOrderDTO(amountTypeVo,orderNo,1,amountTypeVo.getVolumePercentage(),0);
        rcsOrderHideService.doHideOrderHand(hideOrderDTO);
        System.out.println("specialMerchantTest");
    }
//    @Test
    public void specialMerchantTest(){
        String orderNo="4956568718823430212";
        AmountTypeVo amountTypeVo=new AmountTypeVo();
        amountTypeVo.setCategory(OrderHideCategoryEnum.MERCHANT.getId());
        amountTypeVo.setSpecial(SpecialEnum.ordinary.getId());
        amountTypeVo.setVolumePercentage(new BigDecimal(1.0));
        HideOrderDTO hideOrderDTO=new HideOrderDTO(amountTypeVo,orderNo,2,amountTypeVo.getVolumePercentage(),0);
        rcsOrderHideService.doHideOrderHand(hideOrderDTO);
        System.out.println("specialMerchantTest");
    }
//    @Test
    public void specialMerchantTest22(){
        String orderNo="49565687188234302123";
        AmountTypeVo amountTypeVo=new AmountTypeVo();
        amountTypeVo.setCategory(OrderHideCategoryEnum.MERCHANT.getId());
        amountTypeVo.setSpecial(SpecialEnum.ordinary.getId());
        amountTypeVo.setVolumePercentage(new BigDecimal(1.0));
        HideOrderDTO hideOrderDTO=new HideOrderDTO(amountTypeVo,orderNo,1,amountTypeVo.getVolumePercentage(),0);
        rcsOrderHideService.doHideOrderHand(hideOrderDTO);
        System.out.println("specialMerchantTest");
    }
//    @Test
    public void specialMessage(){
        String orderNo="495656871882343021";
        AmountTypeVo amountTypeVo=new AmountTypeVo();
        amountTypeVo.setCategory(OrderHideCategoryEnum.USER.getId());
        amountTypeVo.setSpecial(SpecialEnum.Special.getId());
        amountTypeVo.setVolumePercentage(new BigDecimal(1.0));
        HideOrderDTO hideOrderDTO=new HideOrderDTO(amountTypeVo,orderNo,2,amountTypeVo.getVolumePercentage(),1);
        rcsOrderHideService.doHideOrderHand(hideOrderDTO);
        System.out.println("test_vip_1111");
    }
//    @Test
    public void specialMerchantTest1(){
        String orderNo="495656871882343022";
        AmountTypeVo amountTypeVo=new AmountTypeVo();
        amountTypeVo.setCategory(OrderHideCategoryEnum.USER.getId());
        amountTypeVo.setSpecial(SpecialEnum.Special.getId());
        amountTypeVo.setVolumePercentage(new BigDecimal(1.0));
        HideOrderDTO hideOrderDTO=new HideOrderDTO(amountTypeVo,orderNo,2,amountTypeVo.getVolumePercentage(),0);
        rcsOrderHideService.doHideOrderHand(hideOrderDTO);
        System.out.println("test_1111");
    }
//    @Test
    public void test11(){
        String orderNo="41882343022";
        List<TOrderHidePO> list=new ArrayList<>();
        TOrderHidePO item=new TOrderHidePO();
        item.setOrderNo("41882343022");
        item.setCreateTime(orderHideDal.getNowTime());
        item.setVolumePercentage(new BigDecimal(0.5));
        item.setCategory(1);
        list.add(item);
        item=new TOrderHidePO();
        item.setOrderNo("4188234302322");
        item.setCreateTime(orderHideDal.getNowTime());
        item.setVolumePercentage(new BigDecimal(0));
        item.setCategory(2);
        list.add(item);
        item=new TOrderHidePO();
        item.setOrderNo("41882343023224");
        item.setCreateTime(orderHideDal.getNowTime());
        item.setVolumePercentage(new BigDecimal(1));
        item.setCategory(3);
        list.add(item);
        int rows=orderHideDal.insertOrUpdates(list);
        System.out.println(rows);
    }
    private TOrderHide setTOrderHide(String orderNo, AmountTypeVo amountTypeVo, BigDecimal volumePercentage) {
        TOrderHide orderHide=new TOrderHide();
        orderHide.setOrderNo(orderNo);
        if(OrderHideCategoryEnum.USER.getId().equals(amountTypeVo.getCategory()) &&
                SpecialEnum.Special.getId().equals(amountTypeVo.getSpecial())){
            orderHide.setVolumePercentage(BigDecimal.ZERO);
        }else{
            orderHide.setVolumePercentage(volumePercentage);
        }
        orderHide.setCategory(amountTypeVo.getCategory());
        return orderHide;
    }
}
