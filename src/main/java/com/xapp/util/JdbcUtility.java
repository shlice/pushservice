package com.xapp.util;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcUtility {
    private static JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        JdbcUtility.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @return the jdbcTemplate
     */
    public static JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


}
