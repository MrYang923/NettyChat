package com.tulun.util;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Yang
 * Data:2019/7/31
 * Time:16:08
 */
public class c3p0util {
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource("mysql");

    //从连接池获取连接
    public static Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //释放连接
}
