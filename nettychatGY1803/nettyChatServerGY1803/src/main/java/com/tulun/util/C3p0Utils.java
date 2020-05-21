package com.tulun.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import com.tulun.bean.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/6
 * Time 16:25
 */
public class C3p0Utils {
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource("mysql");

    // 从连接池中取用一个连接
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 释放连接回连接池
    public static void close(Connection conn, PreparedStatement pst, ResultSet rs) {
        if (rs != null) {
            try {
                if (conn != null)
                    conn.close();
                if (rs != null)
                    rs.close();
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
            }
        }
    }

}
