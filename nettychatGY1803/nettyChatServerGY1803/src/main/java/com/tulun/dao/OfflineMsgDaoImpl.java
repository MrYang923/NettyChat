package com.tulun.dao;

import com.tulun.bean.OfflineMsg;
import com.tulun.util.C3p0Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/9
 * Time 10:14
 */
public class OfflineMsgDaoImpl implements OfflineMsgDao {
    @Override
    public void setOfflineMsg(String to_name, String form_name, String msg) {
        Connection con = null;

        try {
            con = C3p0Utils.getConnection();
            String sql = "insert into offline_msg(to_name, from_name, msg) value (?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, to_name);
            ps.setString(2, form_name);
            ps.setString(3, msg);

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public OfflineMsg getOfflineMsgByToName(String to_name) {
       OfflineMsg offlineMsg = null;
        Connection con = null;

        try {
            con = C3p0Utils.getConnection();
            String sql = "select * from offline_msg where to_name = ?";
            //为空，说明数据库中没有该用户名存在
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, to_name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                offlineMsg = new OfflineMsg();
                offlineMsg.setTo_name(rs.getString("to_name"));
                offlineMsg.setFrom_name(rs.getString("from_name"));
                offlineMsg.setMsg(rs.getString("msg"));
            }

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return offlineMsg;
    }
}
