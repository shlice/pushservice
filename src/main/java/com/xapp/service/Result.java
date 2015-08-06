package com.xapp.service;

/**
 * Created by shild on 2015/7/28.
 */
public class Result {
    private String code;

    private String msg;

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result() {
        this.code = "000000";
        this.msg = "操作成功";
    }

    public boolean isSucc() {
        return this.code.equals("000000");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
