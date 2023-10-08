package com.panda.sport.rcs;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.entity.Bet;
import com.panda.sport.data.rcs.dto.oddin.entity.BetStake;
import com.panda.sport.data.rcs.dto.oddin.entity.TicketCustomer;
import com.panda.sport.data.rcs.dto.oddin.entity.TicketSelection;
import com.panda.sport.rcs.enums.oddin.AcceptOddsChangeEnum;
import com.panda.sport.rcs.enums.oddin.BetStakeTypeEnum;
import com.panda.sport.rcs.enums.oddin.TicketChannelEnum;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OddinBootstrap.class)
@Slf4j
public class HighConcurrentTest {

    @Resource
    private TicketOrderService ticketOrderService;
    // 并发请求数
    private static final int threadNum =10;

    private Long orderNo = 888888900000L;
    String selectionId = "od:match:342660/24/4?map=1&threshold=28.5";
    // 倒计时器
    private CountDownLatch cdl =new CountDownLatch(threadNum);

    public class TicketRequest implements Runnable {

        public TicketRequest() {
        }

        public TicketRequest(int no) {
            this.no = no;
        }

        public int no;

        // 重写run方法用于处理业务逻辑
        @Override
        public void run() {
            try {
                cdl.await();
            }catch (Exception e) {
                e.printStackTrace();
            }
            Request<TicketDto> request = new Request<>();
            TicketDto dto = getTicketDto();
            orderNo++;
            dto.setId(orderNo.toString());
            request.setData(dto);
            ticketOrderService.saveOrder(request);
            // todo 业务逻辑...
            System.out.println("now no = " + no);
        }
    }

    @Test
    public void testConcurrent(){
        System.out.println("start ... ");
        for (int i =0; i< threadNum; i++) {
            new Thread(new TicketRequest(i)).start();
            // 倒计时计数
            cdl.countDown();
        }


        try {
            Thread.sleep(20000);
            // 如线程内操作执行脚本，可先阻塞线程，等待子线程执行完成
//            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end ... ");
    }


    private TicketDto getTicketDto() {
        //限额投注实体
        TicketDto ticketDto = new TicketDto();
        //请求限额的UID
        ticketDto.setId("11453228000");
        ticketDto.setSourceId(1);
        //请求限额,投注的UTC时间
        java.sql.Timestamp d = new Timestamp(System.currentTimeMillis());
        ticketDto.setTimestamp(d);
        //目前电竞只支持单关传1
        ticketDto.setTotalCombinations(1);
        //支持语言
        ticketDto.setCurrency("CNY");
        //自动接受赔率的变化
        ticketDto.setAccept_odds_change(AcceptOddsChangeEnum.ACCEPT_ODDS_CHANGE_ANY);
        //来源渠道
        ticketDto.setChannel(TicketChannelEnum.TICKET_CHANNEL_INTERNET);
        //用户信息
        TicketCustomer customer = new TicketCustomer();
        //用户ID
        customer.setId("232323");
        //支持的语言
        customer.setLanguage("zh");

        //注单列表
        Bet bet = new Bet();
        List<Bet> betList = new ArrayList<>();
        //投注实体
        BetStake betStake = new BetStake();
        //投注金额
        betStake.setValue(200);
        //股权类型
        betStake.setType(BetStakeTypeEnum.BET_STAKE_TYPE_SUM);

        Integer[] systems = new Integer[]{1, 66, 102, 111, 108, 100};
        bet.setSystems(systems);
        bet.setSelections(selectionId);
        bet.setStake(betStake);
        betList.add(bet);
        /*ticketDto.setBets(betList);*/

        TicketSelection selection = new TicketSelection();
        selection.setId(selectionId);
        selection.setForeign(false);
        selection.setOdds("1.002");
        Map<String, TicketSelection> selectionsMap = new HashMap<>();
        selectionsMap.put(selection.getId(), selection);

        ticketDto.setSelections(selectionsMap);
        //赋值用户信息
        ticketDto.setCustomer(customer);
        //赋值投注信息
        ticketDto.setBets(betList);
        ticketDto.setLocation_id(Long.valueOf("12345"));

        return ticketDto;
    }
}
