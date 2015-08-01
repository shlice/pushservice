package com.xapp.controller;

import com.alibaba.fastjson.JSON;
import com.xapp.service.RequestHandler;
import com.xapp.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("")
public class AppController {
    private static final Logger logger = LoggerFactory
            .getLogger(AppController.class);

    @ResponseBody
    @RequestMapping(value = "/api", method = RequestMethod.POST)
    public String api(HttpServletRequest request) {
        int totalLength = request.getContentLength();
        byte[] data = new byte[totalLength];
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(request.getInputStream());

            byte[] inputByte = new byte[1024];
            int length = 0;
            int pos = 0;
            while ((length = in.read(inputByte, 0, inputByte.length)) > 0) {
                System.arraycopy(inputByte, 0, data, pos, length);
                pos += length;
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }

        String input = null;
        try {
            input = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
        }

        RequestHandler rh = new RequestHandler(request);
        Result result = rh.handleApi(input);

        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/testapi", method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("testapi");
//        mv.addObject("content", content);
        return mv;
    }


}
