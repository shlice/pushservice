package com.xapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * init spring ctx and jdbcTemplate
 */
public class SpringContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ContextUtility.ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
        JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextUtility.ctx.getBean("jdbcTemplate");
        if (jdbcTemplate != null) {
            JdbcUtility.setJdbcTemplate(jdbcTemplate);
            //logger.info("Spring JdbcTemplate init success!");
        }
        logger.info("spring context init success.");
    }
}
