package com.tulun.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tulun.controller.EnMsgType;
import com.tulun.netty.ClientHandler;
import com.tulun.util.JsonUtils;
import io.netty.channel.Channel;

import java.io.File;
import java.util.Scanner;

import static com.tulun.controller.EnMsgType.EN_MSG_TRANSFER_FILE;

/**
 * 发送服务
 */
public class SendService {
    private Channel channel;
    Scanner scanner = new Scanner(System.in);
    private String localName;


    public SendService(Channel channel) {
        this.channel = channel;
    }

    public void sendMsg() {
        scanner.useDelimiter("\n");
        while (true) {
            loginView();
            String line = scanner.nextLine();
            if ("1".equals(line)) {
                //登录操作
                System.out.println("请输入账号：");
                String name = scanner.nextLine();
                System.out.println("请输入密码：");
                String passwd = scanner.nextLine();

                System.out.println("登录操作：" + "name：" + name + " passwd:" + passwd);
                doLogin(name, passwd);
                continue;
            }
            if ("2".equals(line)) {
                //注册
                System.out.println("请输入账号：");
                String name = scanner.nextLine();
                System.out.println("请输入邮箱：");
                String email = scanner.nextLine();
                while (true) {
                    System.out.println("请输入密码：");
                    String passwd = scanner.nextLine();
                    System.out.println("确认密码：");
                    String passwd1 = scanner.nextLine();
                    if (passwd1.equals(passwd)) {
                        System.out.println("注册操作：" + "name：" + name + "  passwd:" + passwd + " 邮箱：" + email);
                        doRegister(name, passwd, email);
                        break;
                    } else {
                        System.out.println("两次密码输入不一致，请重新输入");
                    }
                }
                continue;
            }
            if ("3".equals(line)) {
                //忘记密码
                System.out.println("请输入忘记密码的账号名：");
                String name = scanner.nextLine();
                System.out.println("输入该用户邮箱");
                String email = scanner.nextLine();
                doFind(name, email);
                continue;
            }
            if ("4".equals(line)) {
                //退出系统
                System.exit(1);
            }
        }

    }

    //忘记密码
    private void doFind(String name, String email) {
        ObjectNode node = JsonUtils.getObjectNode();
        node.put("name", name);
        node.put("email", email);
        node.put("type", String.valueOf(EnMsgType.EN_MSG_FORGET_PWD));
        String msg = node.toString();
        //发送服务端
        channel.writeAndFlush(msg);
        int code = 0;
        try {
            code = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (code == 1) {
            System.out.println("密码已通过邮件发送到邮箱");
        } else {
            System.out.println("邮箱与用户预留邮箱不一致");
        }
    }

    //注册业务逻辑
    private void doRegister(String name, String passwd, String email) {
        ObjectNode node = JsonUtils.getObjectNode();
        node.put("name", name);
        node.put("passwd", passwd);
        node.put("email", email);
        node.put("type", String.valueOf(EnMsgType.EN_MSG_REGISTER));
        String msg = node.toString();
        //发送服务端
        channel.writeAndFlush(msg);

        //等待服务端返回注册结果
        int code = 0;
        try {
            code = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //结果展示：
        if (code == 1) {
            //注册成功
            System.out.println("注册成功,请重新登陆\n");
        } else {
            System.out.println("注册失败,用户名已存在\n");
            //注册失败
            //进入到登录页面
        }
    }

    //登录业务逻辑
    public void doLogin(String name, String passwd) {
        //封装JSON数据
        ObjectNode node = JsonUtils.getObjectNode();
        node.put("name", name);
        node.put("passwd", passwd);
        node.put("type", String.valueOf(EnMsgType.EN_MSG_LOGIN));
        String msg = node.toString();

        //发送服务端
        channel.writeAndFlush(msg);

        //等待服务端返回登录结果
        int code = 0;
        try {
            code = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //结果展示：
        if (code == 200) {
            //登录成功
            this.localName = name;
            showMainMemu();
            while (mainFun()) ;

        } else {
            //登录失败
            //进入到登录页面
        }
    }

    private boolean mainFun() {
        System.out.println("请输入操作：");
        String line = scanner.nextLine();
        String[] split = line.split(" ");

//            "1.输入modifypwdusername 表示该用户要修改密码");
//            "2.输入getallusers 表示用户要查询所有人员信息");
//            "3.输入username:xxx 表示一对一聊天"); //
//            "4.输入all:xxx 表示发送群聊消息");
//            "5.输入sendfile:xxx 表示发送文件请求:[sendfile][接收方用户名][发送文件路径]");
//            "6.输入quit 表示该用户下线，注销当前用户重新登录");
//            "7.输入help查看系统菜单");
        if (split.length == 1) {
            if (split[0].equals("getallusers")) {
                //查询在线用户列表
                System.out.println("getallusers");
            } else if (split[0].equals("quit")) {
                //用户下线操作
                doOffline();
                return false;
            } else if (split[0].equals("help")) {
                //查看系统菜单
                showMainMemu();
            }

        } else if (split.length == 2) {
            if (split[0].equals("modifypwd")) {
                //修改密码操作
                changePwd();
            } else if (split[0].equals("all")) {
                String toMsg = split[1];
                doChatAll(toMsg);
                //群发消息操作
            } else {
                //单聊操作
                String toName = split[0];
                String toMsg = split[1];
                signalChat(toName, toMsg);
            }
        } else if (split.length == 3) {
            if (split[0].equals("sendfile")) {
                String toName = split[1];
                String filePath = split[2];
                sendFile(toName, filePath);
            }
        }
        return true;

    }

    //发送文件
    private void sendFile(String toName, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;

        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EN_MSG_TRANSFER_FILE));
        nodes.put("toName", String.valueOf(toName));
        String msg = nodes.toString();
        channel.writeAndFlush(msg);

        int port = 0;
        try {
            port = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (port > 0){
            //获取端口成功

            //创建子线程：ip,端口，文件
            //启动子线程
            new FileSend(port,file,"127.0.0.1").start();

        }


    }

    //群聊
    private void doChatAll(String toMsg) {
        ObjectNode nodes = JsonUtils.getObjectNode();
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL));
        nodes.put("msg", toMsg);
        nodes.put("fromName", localName);
        String msg = nodes.toString();
        channel.writeAndFlush(msg);
    }

    //用户下线
    private void doOffline() {
        //封装消息
        ObjectNode nodes = JsonUtils.getObjectNode();
        //消息体：消息内容、接收方账号、发送方账号、业务类型
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_OFFLINE));
        String msg = nodes.toString();

        //发送服务器
        channel.writeAndFlush(msg);
    }

    //单聊
    private void signalChat(String toName, String toMsg) {
        //封装消息
        ObjectNode nodes = JsonUtils.getObjectNode();
        //消息体：消息内容、接收方账号、发送方账号、业务类型
        nodes.put("type", String.valueOf(EnMsgType.EN_MSG_CHAT));
        nodes.put("msg", toMsg);
        nodes.put("toName", toName);
        nodes.put("fromName", localName);

        String msg = nodes.toString();

        //发送服务器
        channel.writeAndFlush(msg);
    }

    private void changePwd() {
        System.out.println("输入新密码：");
        String newPwd = scanner.nextLine();
        doAlter(this.localName, newPwd);

    }

    //修改密码
    private void doAlter(String name, String passwd) {
        ObjectNode node = JsonUtils.getObjectNode();
        node.put("name", name);
        node.put("passwd", passwd);
        node.put("type", String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD));
        String msg = node.toString();

        //发送服务端
        channel.writeAndFlush(msg);

        //等待服务端返回登录结果
        int code = 0;
        try {
            code = ClientHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (code == 1) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败");
        }
    }

    //    用户登录页面
    private void loginView() {
        System.out.println("======================");
        System.out.println("1.登录");
        System.out.println("2.注册");
        System.out.println("3.忘记密码");
        System.out.println("4.退出系统");
        System.out.println("======================");
    }


    /**
     * 主菜单页面
     */
    private void showMainMemu() {
        System.out.println("====================系统使用说明====================");
        System.out.println("                         注：输入多个信息用\":\"分割");
        System.out.println("1.输入modifypwd:username 表示该用户要修改密码");
        System.out.println("2.输入getallusers 表示用户要查询所有人员信息");
        System.out.println("3.输入username:xxx 表示一对一聊天"); //
        System.out.println("4.输入all:xxx 表示发送群聊消息");
        System.out.println("5.输入sendfile:xxx 表示发送文件请求:[sendfile][接收方用户名][发送文件路径]");
        System.out.println("6.输入quit 表示该用户下线，注销当前用户重新登录");
        System.out.println("7.输入help查看系统菜单");
        System.out.println("================================================");
    }


}
