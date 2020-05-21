package com.tulun.netty;

import com.tulun.controller.Transfer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ChannelHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(ctx.channel().remoteAddress() + " 2: " + msg);
        Transfer transfer = new Transfer();
        String recv = transfer.process((String) msg, channel);
        //返回为null不做处理
        if (recv != null) {
            System.out.println(recv);
            ctx.channel().writeAndFlush(recv);
        }

    }

    /**
     * 下线操作
     * 异常下线、正常业务逻辑下线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //用户下线，通过channel删除用户缓存信息
        Transfer.removeCacheByChannel(ctx.channel());
    }
}
