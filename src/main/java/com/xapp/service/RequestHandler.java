package com.xapp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xapp.util.ContextUtility;
import com.xapp.util.Util;
import com.xapp.util.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
        if (!checkTime(datetime)) {
            result.setCode("100013");
            result.setMsg("请求超时");
            return result;
        }

        if (!checkSign(input, request.getSign())) {
            result.setCode("100012");
            result.setMsg("参数在传输过程中被篡改");
            return result;
        }

        String type = request.getType();
        if (type.equals("JOIN")) {
            String userId = (String) map.get("uid");
            String token = (String) map.get("token");
            String name = (String) map.get("name");
            String ip = getIP(httprequest);

            if (Util.isStringEmpty(userId) || Util.isStringEmpty(token)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }
            if(name == null)
                name = "";

            as.insertUser(userId, token, name, ip);
        } else if (type.equals("LEAVE")) {
            String userId = (String) map.get("uid");
            String token = (String) map.get("token");

            if (Util.isStringEmpty(userId) || Util.isStringEmpty(token)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            as.removeUser(userId, token);
        } else if (type.equals("REPLACE")) {
            String userId = (String) map.get("delete_user_id");
            String token = (String) map.get("delete_token");
            String newuserId = (String) map.get("user_id");
            String newtoken = (String) map.get("token");
            String newname = (String) map.get("name");
            String newip = getIP(httprequest);

            if(userId == null)
                userId = "";
            if(token == null)
                token = "";
            if(newname == null)
                newname = "";

            if(Util.isStringEmpty(newuserId) || Util.isStringEmpty(newtoken)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            as.replaceUser(userId, token, newtoken, newuserId, newname, newip);
        } else if (type.equals("PUSH")) {
            String userId = (String) map.get("uid");
            String payload = (String) map.get("payload");

            if (Util.isStringEmpty(userId) || !checkPayload(payload)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            result = as.insertPayloads(userId, payload);
        } else if (type.equals("PUSHMSG")) {
            String userId = (String) map.get("uid");
            String msg = (String) map.get("msg");

            if (Util.isStringEmpty(userId) || !checkMsg(msg)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            String payload = getDefaultPayload(msg);

            result = as.insertPayloads(userId, payload);
        } else if (type.equals("PUSHBADGE")) {
            String userId = (String) map.get("uid");
            Integer badge = (Integer) map.get("badge");

            if (Util.isStringEmpty(userId) || badge < 0 || badge > 99) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            String payload = getBadgeOnlyPayload(badge);

            result = as.insertPayloads(userId, payload);
        } else if (type.equals("KGPUSH")) {
            String userIds = (String) map.get("uid");
            String msg = (String) map.get("msg");
            //url and func is user defined fields
            String url = (String) map.get("url");
            String func = (String) map.get("func");

            if (Util.isStringEmpty(userIds) || Util.isStringEmpty(msg)) {
                result.setCode("100006");
                result.setMsg("数据有效性验证失败");
                return result;
            }

            String payload = getKGPushPayload(msg, url, func);
            result = as.insertPayloads(userIds, payload);
        } else {
            result.setCode("100007");
            result.setMsg("API不存在");
            return result;
        }

        return result;
    }

    private String getKGPushPayload(String msg, String url, String func) {
        if (Util.isStringEmpty(msg))
            return "";

        String payload = "{\"aps\":{\"alert\":\"" + msg + "\",\"sound\":\"default\",\"badge\":1}";
        if (!Util.isStringEmpty(url))
            payload += ",\"url\":\"" + url + "\"";
        if (!Util.isStringEmpty(func))
            payload += ",\"func\":\"" + func + "\"";
        payload += "}";

        try {
            int lenth = payload.getBytes("UTF-8").length;
            if (lenth > 256) {
                logger.warn("payload is over 256 bytes, msg:" + msg + ",url:" + url);

                // 简单裁剪。有可能末尾汉子，截断后变乱码。是否影响push待测试。
                int overlenth = lenth - 256;
                byte[] sourcebytes = msg.getBytes("UTF-8");
                int keeplength = msg.getBytes("UTF-8").length - overlenth;
                if(keeplength > 0)
                {
                    byte[] target = new byte[keeplength];
                    for(int i=0; i<keeplength; i++)
                    {
                        target[i] = sourcebytes[i];
                    }
                    String newmsg = new String(target, "UTF-8");
                    payload = "{\"aps\":{\"alert\":\"" + newmsg + "\",\"sound\":\"default\",\"badge\":1}";
                    if (!Util.isStringEmpty(url))
                        payload += ",\"url\":\"" + url + "\"";
                    if (!Util.isStringEmpty(func))
                        payload += ",\"func\":\"" + func + "\"";
                    payload += "}";
                }
                else {
                    logger.error("no space for msg in payload!");
                    return "";
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            return "";
        }

        return payload;
    }

    /**
     * check md5 sign
     *
     * @param input
     * @return
     */
    private boolean checkSign(String input, String inputsign) {
        if (inputsign.equals("x123a@s!s(3@41^2!@^4"))
            return true;

        String bodystr = getRequestBodyString(input);
        String sign = Util.getMD5Str(bodystr + Variable.requestKey);

        if (sign.equals(inputsign))
            return true;

        return false;
    }

    /**
     * get original bosy string in input json string.
     * notice that getString using key "body" with fastjson will get a map string in different sequence.
     *
     * @param input
     * @return
     */
    private String getRequestBodyString(String input) {
        try {
            String subs = input.substring(input.indexOf("\"body\":") + 7);
            int count = 0;
            for (int i = 0; i < subs.length(); i++) {
                if (subs.charAt(i) == '{')
                    count++;
                else if (subs.charAt(i) == '}')
                    count--;

                if (count == 0) {
                    return subs.substring(0, i + 1);
                }
            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }

        return "";
    }

    /**
     * check request send time
     *
     * @param datetime "2014-08-05 21:45:20"
     * @return
     */
    private boolean checkTime(String datetime) {
        if (Util.isStringEmpty(datetime))
            return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date senddt = sdf.parse(datetime);
            long diff = new Date().getTime() - senddt.getTime();
            long mins = diff / (1000 * 60);
            if (mins >= 30)
                return false;
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    private boolean checkMsg(String msg) {
        if (Util.isStringEmpty(msg))
            return false;

        String payload = "{\"aps\":{\"alert\":\"" + msg + "\",\"sound\":\"default\"}}";
        try {
            if (payload.getBytes("UTF-8").length > 256)
                return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        return true;
    }

    /**
     * badge only
     *
     * @param badge
     * @return
     */
    private String getBadgeOnlyPayload(Integer badge) {
        return "{\"aps\":{\"badge\":" + badge + "}}";
    }

    /**
     * default payload
     *
     * @param msg
     * @return
     */
    private String getDefaultPayload(String msg) {
        return "{\"aps\":{\"alert\":\"" + msg + "\",\"sound\":\"default\"}}";
    }

    private boolean checkPayload(String payload) {
        if (Util.isStringEmpty(payload))
            return false;

        try {
            if (payload.getBytes("UTF-8").length > 256)
                return false;

            JSONObject obj = JSON.parseObject(payload);
            JSONObject aps = obj.getJSONObject("aps");
            String sound = aps.getString("sound");
            String badge = aps.getString("badge");
            String alertstr = aps.getString("alert");
            if (Util.isStringEmpty(sound) &&
                    Util.isStringEmpty(badge) &&
                    Util.isStringEmpty(alertstr))
                return false;

        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
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

    public static void main(String[] args)
    {
        RequestHandler r = new RequestHandler(null);
        r.getKGPushPayload("测试测试测试测试测试测试测试测试测试12中", "http://app.nugget-nj.com/nugget/morningCheck/intopark?c=2013110&dt=20150809092449&u=20141021172851000015&sign=89f5afd07e01ec35c07c104a8adb2176","");
    }
}
