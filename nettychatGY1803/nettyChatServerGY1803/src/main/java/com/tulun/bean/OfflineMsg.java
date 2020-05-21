package com.tulun.bean;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Allen
 * Date:2019/8/8
 * Time 22:40
 */
public class OfflineMsg {
    private String to_name;
    private String from_name;
    private String msg;

    @Override
    public String toString() {
        return "OfflineMsg{" +
                "to_name='" + to_name + '\'' +
                ", from_name='" + from_name + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
