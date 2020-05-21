package com.tulun.service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/9
 * Time 19:27
 */

import com.tulun.util.PortUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 文件传输
 */
public class FileTransfer extends Thread {
    private int fromPort;
    private int toPort;

    public FileTransfer(int fromPort, int toPort) {
        this.fromPort = fromPort;
        this.toPort = toPort;
    }

    @Override
    public void run() {
        try {
            //创建serverSocket
            ServerSocket formSS = new ServerSocket();
            ServerSocket toSS = new ServerSocket();

            //绑定端口
            formSS.bind(new InetSocketAddress(fromPort));
            toSS.bind(new InetSocketAddress(toPort));

            //监听端口
            Socket formSocket = formSS.accept();
            Socket toSocket = toSS.accept();

            //文件转发
            InputStream formIS = formSocket.getInputStream();
            OutputStream toOS = toSocket.getOutputStream();
            byte[] bytes = new byte[1024];
            int i=0;
            while ((i=formIS.read(bytes)) != -1) {
                toOS.write(bytes, 0, i);
            }

            //关闭资源
            formSocket.close();
            toSocket.close();
            formSS.close();
            toSS.close();
            //端口管理
            PortUtils.closePort(fromPort);
            PortUtils.closePort(toPort);
            System.out.println("文件转发完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
