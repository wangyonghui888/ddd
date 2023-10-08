package com.panda.rcs.order.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.order.client.ClientCache;
import com.panda.rcs.order.client.ClientManageService;
import com.panda.rcs.order.client.impl.ClientManageServiceImpl;
import com.panda.rcs.order.entity.enums.SubscriptionEnums;
import com.panda.rcs.order.entity.vo.ClientRequestVo;
import com.panda.rcs.order.entity.vo.ClientResponseVo;
import com.panda.rcs.order.utils.ClientResponseUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class PushSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private ClientManageService clientManageService;

    public PushSocketHandler(){
        clientManageService = new ClientManageServiceImpl();
    }

    /**
     * 创建连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientCache.allClientGroupMap.put(ctx.channel().id().toString(), ctx.channel());
        log.error("->Socket连接创建成功，当前连接总数->{}，连接信息->{}", ClientCache.allClientGroupMap.size(), ctx.channel());
    }

    /**
     * 断开连接
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        ClientCache.allClientGroupMap.remove(ctx.channel().id().toString());
        deleteClient(ctx.channel());
        if(ctx.channel() != null){
            ctx.channel().close();
        }
        log.error("->Socket自动断开连接，当前连接总数->{}，连接信息->{}", ClientCache.allClientGroupMap.size(), ctx.channel());
    }

    /**
     * 数据交互
     * @param channelHandlerContext
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        try {
            if(msg instanceof TextWebSocketFrame){
                TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
                if (StringUtils.isNotEmpty(textWebSocketFrame.text())) {
                    ClientRequestVo clientRequest = JSON.parseObject(textWebSocketFrame.text(), ClientRequestVo.class);
                    log.info(":::Socket客户端订阅信息->{},连接客户端信息->{}", clientRequest, channelHandlerContext.channel());
                    if(clientRequest.getCommands() != null && clientRequest.getSubscribe() == null && SubscriptionEnums.CMD_HEARTBEAT_300.getKey().equals(Arrays.asList(clientRequest.getCommands()).get(0))){
                        ClientResponseVo clientResponseVo = ClientResponseUtils.createResponseContext(SubscriptionEnums.CMD_HEARTBEAT_300.getKey(), System.currentTimeMillis(), 0, clientRequest.getUuid(), null, null);
                        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(clientResponseVo)));
                        return;
                    }

                    if((clientRequest.getCommands() == null && clientRequest.getSubscribe() == null) || clientRequest.getMsgId() != null){
                        log.error("::订阅数据不合法，收到数据->{},客户端信息->{}", clientRequest, channelHandlerContext.channel());
                        return;
                    }

                    clientManageService.putClient(channelHandlerContext.channel(), clientRequest);
                    serverReturnMessage(clientRequest, channelHandlerContext.channel());
                }
            } else {
                log.error("--->订阅数据合法，收到数据->{},客户端信息->{}", msg, channelHandlerContext.channel());
            }
            super.channelRead(channelHandlerContext, msg);
        } catch (Exception e){
            log.error("客户端订阅异常，客户端连接信息->{},异常信息={}", channelHandlerContext.channel(), e);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        log.info(":::channelRead0->数据={}，客户端信息={}", textWebSocketFrame, channelHandlerContext.channel());
    }

    /**
     * 心跳检查
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE
                    || event.state() == IdleState.WRITER_IDLE
                    || event.state() == IdleState.ALL_IDLE) {
                log.error("->心跳断连: IdleState:{}, 连接channelId->{}", event.state(), ctx.channel());
                //客户端端连接移除
                ClientCache.allClientGroupMap.remove(ctx.channel().id().toString());
                deleteClient(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    /**
     * 客户端删除
     * @param channel
     */
    private void deleteClient(Channel channel){
       clientManageService.removeClient(channel);
    }

    /**
     * 客户端订阅成功后服务主动发送数据
     * @param clientRequestVo
     * @param channel
     */
    private void serverReturnMessage(ClientRequestVo clientRequestVo, Channel channel){
        Object message = ClientResponseUtils.createResponseContext(SubscriptionEnums.SERVER_ANSWER_MESSAGE.getKey(), null, 0, clientRequestVo.getUuid(), clientRequestVo.getUuid(), null);
        channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(message)));
    }

}
