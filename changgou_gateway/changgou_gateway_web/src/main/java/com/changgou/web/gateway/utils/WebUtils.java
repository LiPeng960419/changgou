package com.changgou.web.gateway.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author: lipeng6@ybm100.com
 * @Date: 2021/03/02 15:32
 */
@Slf4j
public class WebUtils {

    public static String urlEnCode(String url, String charset) {
        try {
            return URLEncoder.encode(url, charset);
        } catch (UnsupportedEncodingException e) {
            log.error("urlEnCode error,url:{},charset:{}", url, charset, e);
            return url;
        }
    }

}