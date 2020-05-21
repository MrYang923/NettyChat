package com.tulun.dao;

import com.tulun.bean.User;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/6
 * Time 20:08
 */
public interface UserDao {
    //登陆，验证修改密码的用户
    User getUserByNamePwd(String name, String pwd);
    //忘记密码得到邮箱
    User getUserByName(String name);
    //注册
    int setUser(String name,String pwd,String email);
    //修改密码
    int updateUserPwd(String name,String pwd);
}
