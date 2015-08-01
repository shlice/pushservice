package com.xapp.util;

import org.springframework.context.ApplicationContext;

public class ContextUtility {
    public static ApplicationContext ctx;

    public static Object getBean(Class<?> clazz) {
        return ctx.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }
}
