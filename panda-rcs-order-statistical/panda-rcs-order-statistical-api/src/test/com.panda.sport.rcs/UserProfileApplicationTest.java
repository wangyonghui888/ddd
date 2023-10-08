//package com.panda.sport.rcs;
//
//import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
//import com.panda.sport.rcs.common.vo.api.response.ListByBetScopeResVo;
//import com.panda.sport.rcs.customdb.entity.DataEntity;
//import com.panda.sport.rcs.customdb.entity.MarketEntity;
//import com.panda.sport.rcs.customdb.entity.MarketOptionEntity;
//import com.panda.sport.rcs.customdb.entity.OddConversionEntity;
//import com.panda.sport.rcs.customdb.mapper.MarketOptionMapper;
//import com.panda.sport.rcs.customdb.mapper.OddConversionMapper;
//import com.panda.sport.rcs.customdb.mapper.OrderOptionOddChangeExtMapper;
//import com.panda.sport.rcs.customdb.service.ISportTypeService;
//import com.panda.sport.rcs.customdb.service.impl.LanguageServiceImpl;
//import com.panda.sport.rcs.customdb.service.impl.StaticsItemServiceImpl;
//import com.panda.sport.rcs.db.entity.OrderOptionOddChange;
//import com.panda.sport.rcs.db.entity.SSport;
//import com.panda.sport.rcs.db.service.IOrderOptionOddChangeService;
//import com.panda.sport.rcs.service.impl.OrderStaticsServiceImpl;
//import com.panda.sport.rcs.service.impl.UserOrderHedgeAnalyzeServiceImpl;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
///**
// * @author :  dorich
// * @project Name :  user_profile
// * @package Name :  com.panda.risk
// * @description :   测试工程
// * @date: 2020-06-20 9:58
// * @modificationHistory Who    When    What
// * --------  ---------  --------------------------
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ApiApplication.class)
//@TestPropertySource({"classpath:/application.properties"})
//public class UserProfileApplicationTest {
//
//    @Autowired
//    OrderStaticsServiceImpl orderStaticsService;
//
//    @Autowired
//    StaticsItemServiceImpl staticsItemService;
//
//    @Autowired
//    UserOrderHedgeAnalyzeServiceImpl userOrderHedgeAnalyzeService;
//
//    @Autowired
//    ISportTypeService sportTypeService;
//
//    @Autowired
//    OrderOptionOddChangeExtMapper orderOptionOddChangeExtMapper;
//
//    @Autowired
//    IOrderOptionOddChangeService orderOptionOddChangeService;
//
//    @Autowired
//    MarketOptionMapper marketOptionMapper;
//
//
//    @Test
//    public void testSport() {
//        List<SSport> lsit = sportTypeService.query();
//        lsit.forEach(e -> System.out.println(e));
//    }
//
//    @Test
//    public void testDataEntity() {
//        DataEntity entity = new DataEntity();
//        entity.setNameCode(123L);
//        System.out.println(entity.getNameCode());
//    }
//
//    @Before
//    public void setUp() throws Exception {
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//
//    @Autowired
//    OddConversionMapper oddConversionMapper;
//
//    @Test
//    public void testOddConversion() {
//        List<OddConversionEntity> odd = oddConversionMapper.getOddConversion();
//        odd.forEach(
//                e -> System.out.println(e));
//    }
//
//    @Test
//    public void testMarketOptionMapper() {
//        Set<Long> arraysList = new HashSet<>();
//        arraysList.add(1221696593737555969L);
//        arraysList.add(1221696593850802178L);
//        arraysList.add(1221696593884356610L);
//        arraysList.add(1221696593968242690L);
//        arraysList.add(1221696594010185731L);
//        List<MarketOptionEntity> list = marketOptionMapper.getMarketOptionByIds(arraysList);
//        list.forEach(
//                e -> System.out.println(e));
//
//    }
//
//    @Autowired
//    LanguageServiceImpl languageService;
//
//    @Test
//    public void testTeamName() {
//        String nameTeam = languageService.getTeamName(1L);
//        String nameTournament = languageService.getTournamentName(1L);
//    }
//
//    @Test
//    public void testBigDecimalRound() {
//        BigDecimal odd = BigDecimal.valueOf(100000).multiply(BigDecimal.valueOf(10.065));
//        System.out.println(odd);
//        BigDecimal euOdd = odd.divide(BigDecimal.valueOf(100000)).setScale(2, BigDecimal.ROUND_DOWN);
//        System.out.println(euOdd);
//
//    }
//
//    @Test
//    public void testMarketMapper() {
//        Set<Long> arraysList = new HashSet<>();
//        arraysList.add(1221696593427177473L);
//        arraysList.add(1221696593527840769L);
//        arraysList.add(1221696593662058497L);
//        arraysList.add(1221696593813053441L);
//        arraysList.add(1221696593926299650L);
//        arraysList.add(1221696594039545858L);
//        arraysList.add(1221696594224095234L);
//        arraysList.add(1221696594391867393L);
//        arraysList.add(1221696594647719938L);
//        arraysList.add(1221696595000041475L);
//        List<MarketEntity> list = marketOptionMapper.getMarketByIds(arraysList);
//        list.forEach(
//                e -> System.out.println(e.getId() + ";" + e.getMarketValue()));
//
//    }
//
//    @Test
//    public void testOderInfoSaveOrUpdate() {
//
//        List<OrderOptionOddChange> entities = new ArrayList<>();
//        OrderOptionOddChange order1 = new OrderOptionOddChange();
//        order1.setBetNo("abcd");
//        order1.setOrderNo("abcd");
//        order1.setPlayOptionsId(1234L);
//        order1.setOddsValue(0);
//        order1.setOrderType(1);
//        OrderOptionOddChange order2 = new OrderOptionOddChange();
//        BeanUtils.copyProperties(order1, order2);
//        entities.add(order1);
//        entities.add(order2);
//        orderOptionOddChangeExtMapper.batchSaveOrUpdate(entities);
//        int count = orderOptionOddChangeService.count();
//        System.out.println(count);
//    }
//
//    @Test
//    public void testListByBetScopeResVo() {
//        ListByBetScopeResVo vo = new ListByBetScopeResVo();
//        vo.setBetType(1);
//        vo.setName("");
//        System.out.println(vo);
//    }
//
//    public static int oddWideClassify(BigDecimal odd) {
//        /*** TODO 盘口类型(OU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘） ***/
//        return 0;
//    }
//
//    public static String betAmountWideClassify(BigDecimal amount) {
//        /*** TODO 搞清楚下注额的单位,将其转换为对应的 单位为元的数据 ***/
//        long betAmount = amount.longValue();
//        /***暂时认为下注额单位是分***/
//        if (betAmount < 1000) {
//            return "1000";
//        } else if (betAmount < 2000) {
//            return "2000";
//        } else if (betAmount < 5000) {
//            return "5000";
//        } else if (betAmount < 10000) {
//            return "10000";
//        }
//        return "10000+";
//    }
//
//    @Test
//    public void testGroupBy() {
//
//    }
//
//
//    @Test
//    public void testStaticsMapper() {
//        orderStaticsService.staticsUserOrder(1);
//    }
//
//    @Test
//    public void testOdds() {
//        for (int i = 1; i < 1000; i++) {
//            BigDecimal mOdds = new BigDecimal(0).subtract(BigDecimal.valueOf(i).divide(BigDecimal.valueOf(100000), 7, RoundingMode.HALF_DOWN));
//            System.out.println("欧赔:" + i + ";马赔:" + mOdds);
//        }
//    }
//
//    @Test
//    public void getEuropeOdd() {
//        for (int i = 1; i < 100000; i++) {
//            BigDecimal toChangeValue = new BigDecimal(i).divide(BigDecimal.valueOf(100));
//            BigDecimal value = BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(1).divide(toChangeValue, 7, RoundingMode.HALF_DOWN));
//            System.out.println("马:" + i + ";欧:" + value);
//        }
//    }
//
//    @Test
//    public void testString() {
//
//        String key = String.format("%s.%s", 789, "statics.ip.lasttime");
//        String tip = String.format("用户(uid:%s)订单对冲分析失败.", 789);
//        System.out.println(tip);
//    }
//
//
//    @Test
//    public void testStaticsOrderForUsers() {
//        orderStaticsService.staticsOrderForUsers(1579934635993l);
//    }
//
//    @Test
//    public void testHedgeAnalyze() {
//        userOrderHedgeAnalyzeService.analyzeUserOrderHedge(1579949752147L);
//    }
//
//    @Test
//    public void testHedgeStatics() {
//        /***
//         * 1. 增加2个足球的订单,3个订单.其中某2个订单属于同一个盘口; (数据库中完成)
//         * 2. 调用统计功能处理;
//         * 构造的数据信息:
//         *
//         uid = 129403542301253632;
//         betTime = 1579862752147;
//         betAmount =  20000
//         * ***/
//        // orderStaticsService.staticsOrderForUsers(1579949752147L);
//        orderStaticsService.staticsOrderForUsers(157986275214L);
//    }
//
//
//    @Test
//    public void testTimeZone() {
//        final long millSecondDay = 24 * 3600 * 1000;
//        Long timeStamp = System.currentTimeMillis();
//        long timeTest = (timeStamp / millSecondDay) * millSecondDay;
//        LocalDateTimeUtil.getDayStartTime(timeTest);
//    }
//
//    @Test
//    public void testHudge() {
//        long timeStamp = 1596585600846L;
//        long actualTime = 1593820800000L;
//        long first = LocalDateTimeUtil.getDayStartTime(timeStamp);
//        long second = LocalDateTimeUtil.dayMill;
//        long timeStampBegin = LocalDateTimeUtil.getDayStartTime(timeStamp) - LocalDateTimeUtil.dayMill;
//        /*** {"userId":"178358382546591744","beginDate":1593878400000,"endDate":1596297600000}  ***/
//        orderStaticsService.staticsUserOrder(199262646579961856L, timeStamp);
//    }
//
//
//
//}
