package com.tulun.netty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tulun.controller.EnMsgType;
import com.tulun.service.FileRecv;
import com.tulun.util.JsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    //同步阻塞队列，将服务端返回给工作线程的数据传输给主线程处理
    public static SynchronousQueue<Integer> queue = new SynchronousQueue<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 2: " + msg);
        //解析服务端返回数据
        String recvMsg = (String) msg;
        ObjectNode jsonNodes = JsonUtils.getObjectNode(recvMsg);
        String type = jsonNodes.get("type").asText();
        if (String.valueOf(EnMsgType.EN_MSG_ACK).equals(type)) {
            //ack消息
            String srctype = jsonNodes.get("srctype").asText();
            if (String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE).equals(srctype)){
                //文件操作 发送端的返回操作
                int port = jsonNodes.get("port").asInt();
                queue.put(port);
            }
            if (String.valueOf(EnMsgType.EN_MSG_LOGIN).equals(srctype)) {
                //登录操作的ack消息
                int code = jsonNodes.get("code").asInt();
                //将服务端的返回交给主线程
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(srctype)) {
                System.out.println("消息已发送");
            }
            if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(srctype)) {
                //发送方的逻辑
                int code = jsonNodes.get("code").asInt();
                if (code == 200) {
                    System.out.println("接收方在线，消息转发成功");
                } else if (code == 300) {
                    System.out.println("接收方不在线");
                }
            }
            if (String.valueOf(EnMsgType.EN_MSG_OFFLINE).equals(srctype)) {
                System.out.println("用户已下线");
            }
            if (String.valueOf(EnMsgType.EN_MSG_REGISTER).equals(srctype)) {
                //注册操作的ack消息
                int code = jsonNodes.get("code").asInt();
                //将服务端的返回交给主线程
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_FORGET_PWD).equals(srctype)) {
                //忘记密码操作的ack消息
                int code = jsonNodes.get("code").asInt();
                //将服务端的返回交给主线程
                queue.put(code);

            }
            if (String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD).equals(srctype)) {
                //修改密码操作的ack消息
                int code = jsonNodes.get("code").asInt();
                //将服务端的返回交给主线程
                queue.put(code);
            }
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(type)) {
            //接收方的逻辑
            String msg1 = jsonNodes.get("msg").asText();
            String formName = jsonNodes.get("fromName").asText();
            System.out.println(formName + ":" + msg1);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(type)) {
            //接收方的逻辑
            String msg1 = jsonNodes.get("msg").asText();
            String formName = jsonNodes.get("fromName").asText();
            System.out.println(formName + ":" + msg1);
        }
        if (String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE).equals(type)){
            //接受端接受文件的操作
            //创建子线程
            int port = jsonNodes.get("port").asInt();
            new FileRecv(port).start();
        }
    }
}
