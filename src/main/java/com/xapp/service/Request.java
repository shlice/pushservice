package com.xapp.service;

import java.util.Map;

/**
 * Created by shild on 2015/7/27.
 */
public class Request {
    public String type;

    public Map body;

    public String sign;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map getBody() {
        return body;
    }

    public void setBody(Map body) {
        this.body = body;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
