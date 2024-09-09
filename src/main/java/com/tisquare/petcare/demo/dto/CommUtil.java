package com.tisquare.petcare.demo.dto;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CommUtil {

    /*private static Logger logger = LoggerFactory.getLogger(CommUtil.class);

    //customer header.host
    public HeaderDto.Source getSource(String service) {
        String hostName = null;
        String ipAddress = null;

        try {
            hostName = InetAddress.getLocalHost().getHostName();
            ipAddress = InetAddress.getLocalHost().getHostAddress();

        } catch (UnknownHostException e) {
            logger.info("Failed to get information: {}", e.getMessage());
        }

        return new HeaderDto.Source(service, hostName, ipAddress);
        //headerDto 객체 생성 후 header.setSource(LampUtil.getSource("service명")
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        logger.info("ip: {}", ip);

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;

    }

    private String getAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        logger.info("agent: {}", userAgent);

        String agent = getAgent(userAgent);
        return agent;
    }

    public String getAgent(String userAgent) {
        String agent = "";

        if (userAgent.contains("Trident/7.0")) {
            agent = "Internet Explorer 11";
        } else if (userAgent.contains("MSIE 10")) {
            agent = "Internet Explorer 10";
        } else if (userAgent.contains("MSIE 9")) {
            agent = "Internet Explorer 9";
        } else if (userAgent.contains("MSIE 8")) {
            agent = "Internet Explorer 8";
        } else if (userAgent.contains("Edg/")) {
            agent = "Microsoft Edge";
        } else if (userAgent.contains("Chrome/")) {
            agent = "Chrome";
        } else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) {
            agent = "Safari";
        } else if (userAgent.contains("Firefox/")) {
            agent = "Firefox";
        } else if (userAgent.contains("Opera/") || userAgent.contains("OPR/")) {
            agent = "Opera";
        } else {
            agent = "Other";
        }

        return agent;
    }*/

}
