package com.infinity.ai.platform.common;

import io.ipinfo.api.IPinfo;
import io.ipinfo.api.model.IPResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IPUtil {

    public static final String token = "42b878ab610ab1";

    public static final IPinfo ipInfo = new IPinfo.Builder()
            .setToken(token)
            .build();

    public static String getIpAddress(String ip) {
        try {
            IPResponse response = ipInfo.lookupIP(ip);
            if (response.getCountryName() != null) {
                return response.getCountryName();
            }
        } catch (Exception ex) {
            log.error("get ip address error", ex);
        }
        return "unknown";
    }
}
