package com.tulun.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/13
 * Time 17:57
 */
public class FileRecv extends Thread {
    private int port;
    private String ip = "127.0.0.1";

    public FileRecv(int port) {
        this.port = port;
    }
    @Override
    public void run() {
        try {
            //创建socket
            Socket socket = new Socket();

            //连接服务器
            socket.connect(new InetSocketAddress(ip, port));

            //接收文件
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String fileName = dataInputStream.readUTF();

            //默认路劲
            String savePath ="C:\\Users\\deng\\Desktop\\chat\\"+fileName;
            File file = new File(savePath);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = dataInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0 ,i);
            }

            //关闭资源
            outputStream.close();
            dataInputStream.close();
            socket.close();

            System.out.println("文件："+fileName + " 接收完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
