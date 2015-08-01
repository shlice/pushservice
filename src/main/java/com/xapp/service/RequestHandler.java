package com.xapp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xapp.util.ContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by shild on 2015/7/27.
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory
            .getLogger(RequestHandler.class);

    private HttpServletRequest httprequest = null;

    public RequestHandler(HttpServletRequest request) {
        this.httprequest = request;
    }

    public Result handleApi(String input) {
        logger.info(input);

        Request request = JSON.parseObject(input, Request.class);

        AppService as = (AppService) ContextUtility.getBean("appService");

        Result result = new Result();

        HashMap map = (HashMap) request.getBody();
        if (map.isEmpty()) {
            result.setCode("100016");
            result.setMsg("参数体body不能为空");
            return result;
        }

        String datetime = (String) map.get("dateTime");
        if(!checkTime(datetime))
        {
            result.setCode("100013");
            result.setMsg("请求超时");
            return result;
        }

        String type = request.getType();
        if (type.equals("JOIN")) {
            String userId = (String) map.get("user_id");
            String token = (String) map.get("token");
            String name = (String) map.get("name");
            String ip = getIP(httprequest);

            if (userId.isEmpty() || token.isEmpty()) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            as.insertUser(userId, token, name, ip);
        } else if (type.equals("UPDATE")) {
            String userId = (String) map.get("user_id");
            String token = (String) map.get("token");

            if (userId.isEmpty() || token.isEmpty()) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            as.updateToken(userId, token);
        } else if (type.equals("LEAVE")) {
            String userId = (String) map.get("user_id");

            if (userId.isEmpty()) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            as.removeUser(userId);
        } else if (type.equals("PUSH")) {
            String userId = (String) map.get("user_id");
            String payload = (String) map.get("payload");

            if (userId.isEmpty() || !checkPayload(payload)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            result = as.insertPayload(userId, payload);
        } else if (type.equals("PUSHMSG")) {
            String userId = (String) map.get("user_id");
            String msg = (String) map.get("msg");

            if (userId.isEmpty() || !checkMsg(msg)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            String payload = getDefaultPayload(msg);

            result = as.insertPayload(userId, payload);
        } else if (type.equals("PUSHBADGE")) {
            String userId = (String) map.get("user_id");
            Integer badge = (Integer) map.get("badge");

            if (userId.isEmpty() || badge < 0 || badge > 99) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            String payload = getBadgeOnlyPayload(badge);

            result = as.insertPayload(userId, payload);
        } else {
            result.setCode("100007");
            result.setMsg("API不存在");
            return result;
        }

        return result;
    }

    /**
     * check request send time
     * @param datetime "2014-08-05 21:45:20"
     * @return
     */
    private boolean checkTime(String datetime) {
        if(datetime == null || datetime.isEmpty())
            return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date senddt = sdf.parse(datetime);
            long diff = new Date().getTime() - senddt.getTime();
            long mins = diff / (1000 * 60);
            if(mins >= 30)
                return false;
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    private boolean checkMsg(String msg) {
        if(msg == null || msg.isEmpty())
            return false;

        String payload = "{\"aps\":{\"alert\":\"" + msg + "\",\"sound\":\"default\"}}";
        try {
            if(payload.getBytes("UTF-8").length > 256)
                return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        return true;
    }

    /**
     * badge only
     * @param badge
     * @return
     */
    private String getBadgeOnlyPayload(Integer badge) {
        return "{\"aps\":{\"badge\":" + badge + "}}";
    }

    /**
     * default payload
     * @param msg
     * @return
     */
    private String getDefaultPayload(String msg) {
        return "{\"aps\":{\"alert\":\"" + msg + "\",\"sound\":\"default\"}}";
    }

    private boolean checkPayload(String payload) {
        if (payload == null || payload.isEmpty())
            return false;

        try {
            if (payload.getBytes("UTF-8").length > 256)
                return false;

            JSONObject obj = JSON.parseObject(payload);
            JSONObject aps = obj.getJSONObject("aps");
            String sound = aps.getString("sound");
            String badge = aps.getString("badge");
            String alertstr = aps.getString("alert");
            if ((sound == null || sound.isEmpty()) &&
                    (badge == null || badge.isEmpty()) &&
                    (alertstr == null || alertstr.isEmpty()))
                return false;

        } catch (UnsupportedEncodingException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private String getIP(HttpServletRequest request) {
        //获取访问者IP
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            ;
        } else {
            ip = request.getHeader("X-Forwarded-For");
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index != -1) {
                    ip = ip.substring(0, index);
                } else {
                    ;
                }
            } else {
                ip = request.getRemoteAddr();
            }
        }

        return ip;
    }
}
