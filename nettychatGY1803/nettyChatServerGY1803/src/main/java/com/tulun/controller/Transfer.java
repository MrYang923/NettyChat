package com.tulun.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tulun.bean.User;
import com.tulun.cantant.EnMsgType;
import com.tulun.dao.OfflineMsgDaoImpl;
import com.tulun.dao.UserDaoImpl;
import com.tulun.service.FileTransfer;
import com.tulun.util.EmailUtils;
import com.tulun.util.JsonUtils;

import com.tulun.util.PortUtils;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Transfer {
    //缓存
    private static ConcurrentHashMap<String, Channel> sc = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Channel, String> cs = new ConcurrentHashMap<>();

    //消息解析器
    public String process(String msg, Channel channel) {

        ObjectNode objectNode = JsonUtils.getObjectNode(msg);
        //解析数据类型
        String type = objectNode.get("type").asText();
        if (String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE).equals(type)) {
            return sendFile(objectNode);
        }
        if (String.valueOf(EnMsgType.EN_MSG_LOGIN).equals(type)) {
            return doLogin(objectNode, channel);
        }
        //注册请求
        if (String.valueOf(EnMsgType.EN_MSG_REGISTER).equals(type)) {
            return doRegister(objectNode);
        }
        //忘记密码
        if (String.valueOf(EnMsgType.EN_MSG_FORGET_PWD).equals(type)) {
            return doForget(objectNode);
        }
        //修改密码
        if (String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD).equals(type)) {
            return doModifyPwd(objectNode);
        }
        //单聊
        if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(type)) {
            return signalChat(objectNode);
        }
        //下线
        if (String.valueOf(EnMsgType.EN_MSG_OFFLINE).equals(type)) {
            return offLine(objectNode, channel);
        }
        //群聊
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(type)) {
            return chatAll(objectNode);
        }


        return null;
    }

    //发送文件
    private String sendFile(ObjectNode objectNode) {
        //封装发送端的返回消息
        ObjectNode fromJson = JsonUtils.getObjectNode();
        fromJson.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        fromJson.put("srctype", String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE));

        //发送端
        int fromPort = PortUtils.getFreePort();
        fromJson.put("port",fromPort);
        String fromMsg = fromJson.toString();


        //解析json字段，获取接受端
        String toName = objectNode.get("toName").asText();
        Channel toChannel = isExist(toName);
        if (toChannel != null) {
            //接受端在线
            //端口分配
            //接受端
            int toPort = PortUtils.getFreePort();

            //创建子线程 端口绑定
            new FileTransfer(fromPort, toPort).start();

            //封装接受端的消息
            ObjectNode toJson = JsonUtils.getObjectNode();
            toJson.put("type", String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE));
            toJson.put("port", toPort);
            String toMsg = toJson.toString();
            toChannel.writeAndFlush(toMsg);
        } else {
            //不在线
        }


        return fromMsg;
    }

    private String chatAll(ObjectNode objectNode) {
        String msg = objectNode.get("msg").asText();
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        nodes.put("srctype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL));
        Iterator<Channel> onlineUser = cs.keySet().iterator();
        while (onlineUser.hasNext()) {
            Channel channel = onlineUser.next();
            channel.writeAndFlush(objectNode.toString());
        }
        return nodes.toString();
    }

    //用户下线
    private String offLine(ObjectNode objectNode, Channel channel) {
        removeCacheByChannel(channel);
        //封装返回数据类型
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        nodes.put("srctype", String.valueOf(EnMsgType.EN_MSG_OFFLINE));
        String recvMsg = nodes.toString();

        return recvMsg;
    }

    //一对一聊天
    private String signalChat(ObjectNode objectNode) {
        String msg = objectNode.get("msg").asText();
        //接收方
        String toName = objectNode.get("toName").asText();
        String fromName = objectNode.get("fromName").asText();

        //查找接收方是否在线
        Channel channel = isExist(toName);

        //封装返回类型
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        nodes.put("srctype", String.valueOf(EnMsgType.EN_MSG_CHAT));

        if (channel != null) {
            //接收方在线
            //将消息转发给接收方
            channel.writeAndFlush(objectNode.toString());

            nodes.put("code", 200);
        } else {
            //接收方不在线
            //自己实现
            OfflineMsgDaoImpl offlineMsg = new OfflineMsgDaoImpl();
            offlineMsg.setOfflineMsg(toName, fromName, msg);

            //消息存储
            nodes.put("code", 300);
        }

        return nodes.toString();
    }

    //缓存存储
    public static void putCache(String name, Channel channel) {
        if (sc.get(name) == null) {
            //不存在
            sc.put(name, channel);
            cs.put(channel, name);
        }
    }


    //删除缓存
    public static void removeCacheByChannel(Channel channel) {
        String name = cs.get(channel);
        if (name != null) {
            sc.remove(name);
            cs.remove(channel);
        }
    }

    //判断缓存数据是否存在
    public static Channel isExist(String name) {
        return sc.get(name);
    }

    //登陆
    private String doLogin(ObjectNode objectNode, Channel channel) {
        UserDaoImpl handle = new UserDaoImpl();
        User user = null;
        int state = 0;
        //登录请求
        String name = objectNode.get("name").asText();
        String passwd = objectNode.get("passwd").asText();
        System.out.println("登录操作：name:" + name + "， passwd:" + passwd);
        //数据库操作判断登录是否成功
        user = handle.getUserByNamePwd(name, passwd);
        if (user != null) {
            putCache(name, channel);
            state = 200;
        }
        //封装返回数据类型
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        nodes.put("srctype", String.valueOf(EnMsgType.EN_MSG_LOGIN));
        nodes.put("code", state);
        String recvMsg = nodes.toString();

        return recvMsg;
    }

    //注册
    private String doRegister(ObjectNode objectNode) {
        UserDaoImpl handle = new UserDaoImpl();
        int state = 0;
        String name = objectNode.get("name").asText();
        String passwd = objectNode.get("passwd").asText();
        String email = objectNode.get("email").asText();
        System.out.println("注册操作：name" + name + "， passwd:" + passwd + ",email:" + email);
        //数据库写入信息
        state = handle.setUser(name, passwd, email);
        //封装返回数据类型
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        nodes.put("srctype", String.valueOf(EnMsgType.EN_MSG_REGISTER));
        nodes.put("code", state);
        String recvMsg = nodes.toString();
        return recvMsg;
    }

    //忘记密码
    private String doForget(ObjectNode objectNode) {
        UserDaoImpl handle = new UserDaoImpl();
        User user = null;
        int state = 0;
        String name = objectNode.get("name").asText();
        String email = objectNode.get("email").asText();
        user = handle.getUserByName(name);
        String userEmail = user.getEmail();
        String userPwd = user.getPwd();
        if (email.equals(userEmail)) {
            try {
                EmailUtils.toEmail(userEmail, userPwd);
                System.out.println("发了");
                state = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        nodes.put("srctype", String.valueOf(EnMsgType.EN_MSG_FORGET_PWD));
        nodes.put("code", state);
        String recvMsg = nodes.toString();
        return recvMsg;
    }

    //修改密码
    private String doModifyPwd(ObjectNode objectNode) {
        UserDaoImpl handle = new UserDaoImpl();
        int state = 0;
        //登录请求
        String name = objectNode.get("name").asText();
        String passwd = objectNode.get("passwd").asText();

        //数据库操作判断登录是否成功
        state = handle.updateUserPwd(name, passwd);

        //封装返回数据类型
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_ACK));
        nodes.put("srctype", String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD));
        nodes.put("code", state);
        String recvMsg = nodes.toString();

        return recvMsg;
    }
}
