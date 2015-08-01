package com.xapp.service;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

//@Transactional
public class AppService {
    @Resource
    JdbcTemplate jdbcTemplate;

    public void insertUser(String userid, String token, String name, String ip) {
        int count = jdbcTemplate.queryForInt("select count(*) from active_users where user_id = ?", userid);
        if (count == 0) {
            jdbcTemplate
                    .update("insert into active_users (user_id, device_token, name, ip_address) values (?,?,?,?);", userid, token, name, ip);
        } else {
            //update
            jdbcTemplate
                    .update("update active_users set device_token = ?,ip_address = ? where user_id=?", token, ip, userid);
        }
    }

    public void updateToken(String userid, String token) {
        jdbcTemplate
                .update("update active_users set device_token = ? where user_id=?", token, userid);

    }

    public void removeUser(String userid) {
        jdbcTemplate
                .update("delete from active_users where user_id=?", userid);
    }

    public Result insertPayload(String userId, String payload) {
//        String token = jdbcTemplate.queryForObject("select device_token from active_users where user_id=?", String.class, userId);
        List<String> list = jdbcTemplate.queryForList("select device_token from active_users where user_id=?", String.class, userId);

        Result result = new Result();

        if (list.size() == 0) {
            result.setCode("100009");
            result.setMsg("用户不存在");
            return result;
        }

        jdbcTemplate.update("insert into push_queue (device_token, payload, time_queued, time_sent) values (?,?,?,?)",
                list.get(0), payload, new Date(), null);

        return result;
    }
}
