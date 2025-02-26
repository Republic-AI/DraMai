package com.infinity.ai.telegram.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class CatBotConfig {
    //机器人名称
    private String botUserName;
    //机器人token
    private String botToken;
    //webHook路径
    private String webHookPath;
    //游戏 url
    private String gameUrl;
    //游戏名称
    private String gameName;
    //本服务的域名
    private String tgUrl;
    //默认的渠道来源
    private String defaultSrc;
}
