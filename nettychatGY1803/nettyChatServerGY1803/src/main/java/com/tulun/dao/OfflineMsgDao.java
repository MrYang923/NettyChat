package com.tulun.dao;

import com.tulun.bean.OfflineMsg;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/9
 * Time 10:10
 */
public interface OfflineMsgDao {
    void setOfflineMsg(String to_name,String from_name,String msg);
    OfflineMsg getOfflineMsgByToName(String to_name);
}
