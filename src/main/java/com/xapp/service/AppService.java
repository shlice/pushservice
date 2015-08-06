package com.xapp.service;

import com.xapp.util.Util;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

//@Transactional
public class AppService {
    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * insertUser or updateUser
     * primary key is (userid,token)
     * @param userid not concerned with device
     * @param token
     * @param name
     * @param ip
     */
    public void insertUser(String userid, String token, String name, String ip) {
        int count = jdbcTemplate.queryForInt("select count(*) from active_users where user_id = ? and device_token = ?", userid, token);
        if (count == 0) {
            //new
            jdbcTemplate
                    .update("insert into active_users (user_id, device_token, name, ip_address) values (?,?,?,?);", userid, token, name, ip);
        } else {
            //update
            jdbcTemplate
                    .update("update active_users set name=?, ip_address = ? where user_id=? and device_token = ?", name, ip, userid, token);
        }
    }

    /*
    public void updateToken(String userid, String token) {
        jdbcTemplate
                .update("update active_users set device_token = ? where user_id=?", token, userid);

    }
    */

    public void removeUser(String userid, String token) {
        jdbcTemplate
                .update("delete from active_users where user_id=? and device_token=?", userid, token);
    }

    public void removeToken(String token) {
        jdbcTemplate
                .update("delete from active_users where device_token=?", token);
    }

    /**
     * send apns payload to user.(not single device)
     * @param userId
     * @param payload
     * @return
     */
    private Result insertPayload(String userId, String payload) {
//        String token = jdbcTemplate.queryForObject("select device_token from active_users where user_id=?", String.class, userId);
        List<String> list = jdbcTemplate.queryForList("select device_token from active_users where user_id=?", String.class, userId);

        Result result = new Result();

        if (list.size() == 0) {
            result.setCode("100009");
            result.setMsg("用户不存在");
            return result;
        }

        for(int i=0; i<list.size(); i++) {
            jdbcTemplate.update("insert into push_queue (device_token, payload, time_queued, time_sent) values (?,?,?,?)",
                    list.get(i), payload, new Date(), null);
        }

        return result;
    }


    /**
     * send apns payload to several users
     * @param userids separated by ,
     * @param payload
     * @return
     */
    public Result insertPayloads(String userids, String payload) {
        Result result = new Result();

        if (Util.isStringEmpty(userids)) {
            result.setCode("100009");
            result.setMsg("用户不存在");
            return result;
        }

        String[] strarr = userids.split(",");
        int succ_count = 0;
        for(int i=0; i<strarr.length; i++)
        {
            result = insertPayload(strarr[i].trim(), payload);
            if(result.isSucc())
                succ_count++;
        }

        if(succ_count == 0) {
            result.setCode("100009");
            result.setMsg("用户不存在");
            return result;
        }
        else if(succ_count < strarr.length) {
            result.setCode("100000");
            result.setMsg("操作成功。成功用户数：" + succ_count);
            return result;
        }

        // all succ
        return result;
    }
}
