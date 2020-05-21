package com.tulun.dao;

import com.tulun.bean.User;
import com.tulun.util.C3p0Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/6
 * Time 20:08
 */
public class UserDaoImpl implements UserDao {
    @Override
    public User getUserByNamePwd(String name, String pwd) {
        User user = null;
        Connection con = null;

        try {
            con = C3p0Utils.getConnection();
            String sql = "select * from user where name=? and pwd=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, pwd);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPwd(rs.getString("pwd"));
                user.setEmail(rs.getString("email"));
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

        return user;
    }

    @Override
    public User getUserByName(String name) {
        User user = null;
        Connection con = null;

        try {
            con = C3p0Utils.getConnection();
            String sql = "select * from user where name=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPwd(rs.getString("pwd"));
                user.setEmail(rs.getString("email"));
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

        return user;
    }

    @Override
    public int setUser(String name, String pwd, String email) {
        User isHaveUser = null;
        Connection con = null;
        int result = 0;

        try {
            con = C3p0Utils.getConnection();
            String sql = "insert into user(name, pwd, email) value (?,?,?)";
            isHaveUser = getUserByName(name);
            //为空，说明数据库中没有该用户名存在
            if (isHaveUser == null) {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, pwd);
                ps.setString(3, email);
                result = ps.executeUpdate();
                return result;
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
        return result;
    }

    @Override
    public int updateUserPwd(String name, String pwd) {
        User user = null;
        Connection con = null;
        int result = 0;
        try {
            con = C3p0Utils.getConnection();
            String sql = "update user set pwd = ? where name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, pwd);
            ps.setString(2, name);
            result = ps.executeUpdate();
            return result;

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
        return result;
    }


    public static void main(String[] args) {
        UserDaoImpl userDao = new UserDaoImpl();
        System.out.println(userDao.getUserByNamePwd("allen","123456"));
    }
}
