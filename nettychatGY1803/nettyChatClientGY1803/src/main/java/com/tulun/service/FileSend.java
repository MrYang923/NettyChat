package com.tulun.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/9
 * Time 19:43
 */
public class FileSend extends Thread {
    private int port;
    private File file;
    private String ip;

    public FileSend(int port, File file, String ip) {
        this.port = port;
        this.file = file;
        this.ip = ip;
    }

    @Override
    public void run() {

        try {
            //创建socket
            Socket socket = new Socket();

            //连接服务端
            socket.connect(new InetSocketAddress(ip, port));

            //通过流封装文件名及文件本身
            String fileName = file.getName();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //封装文件名
            dataOutputStream.writeUTF(fileName);

            //封装文件内容
            byte[] bytes = new byte[1024];
            FileInputStream inputStream = new FileInputStream(file);
            int i ;
            //发送文件
            while ((i= inputStream.read(bytes)) != -1) {
                dataOutputStream.write(bytes,0 ,i);
            }


            dataOutputStream.flush();
            //关闭资源
            dataOutputStream.close();
            socket.close();
            System.out.println("文件传输完成");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
