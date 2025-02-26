package com.infinity.ai.platform.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infinity.ai.platform.entity.dto.TwitterDto;
import com.infinity.ai.platform.entity.dto.TwitterUserDto;
import com.infinity.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class TwitterManager {
    @Value("${twitter.clientId}")
    private String clientId;

    @Value("${twitter.clientSecret}")
    private String clientSecret;

    @Value("${twitter.redirectUrl}")
    private String redirectUrl;

    @Value("${twitter.consumerKey}")
    private String consumerKey;

    @Value("${twitter.consumerSecret}")
    private String consumerSecret;

    /**
     * 根据code获取用户token
     *
     * @param code         根据code换取token
     * @param refreshToken 根据token刷新token
     * @return
     */
    public TwitterDto requestBearerToken(String code, String refreshToken, String state) {
        try {
            log.info("requestBearerToken code: {}, refreshToken: {}, state: {}", code, refreshToken, state);
            String clientId = URLEncoder.encode(this.clientId, "UTF-8");
            String clientSecret = URLEncoder.encode(this.clientSecret, "UTF-8");
            //商户id和商户的私钥
            String credentials = clientId + ":" + clientSecret;
            //对商户id和私钥机密一下
            String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            //这个地址一定要和获取code链接里面填的回调地址保持一致(恶心)
            String redirectURI = redirectUrl;
            //请求、换取Token的地址
            URL url = new URL("https://api.twitter.com/2/oauth2/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + base64Credentials);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            String data = "";
            if (StringUtils.isBlank(refreshToken)) {
                //authorization_code、refresh_token、client_credentials
                //我们这里是根据code换取Token所以选择authorization_code
                String grantType = "authorization_code";
                //获取令牌需要的参数    code_verifier 需要和获取code传入参数 code_challenge 保持一致
                data = "grant_type=" + grantType + "&code=" + code + "&redirect_uri=" + redirectURI + "&client_id=" + this.clientId + "&client_secret=" + this.clientSecret + "&code_verifier=" + "0ioze5m20493ny2";
            } else {
                //下面是通过刷新令牌的Token去换取新的Token，防止Token过期
                //authorization_code、refresh_token、client_credentials
                String grantType = "refresh_token";
                //刷新令牌需要的参数
                data = "refresh_token=" + refreshToken + "&grant_type=" + grantType + "&client_id=" + this.clientId + "&client_secret=" + this.clientSecret + "&code_verifier=challenge";
            }
            log.info("twitter request: {}", data);

            connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            String jsonResponse = response.toString();
            JSONObject json = JSON.parseObject(jsonResponse);
            log.info("twitter response: {}", json);
            TwitterDto twitterDto = new TwitterDto();
            //我们实际使用Token
            //当我们使用的Token过期以后可以用这个来换取新的Token
            twitterDto.setAccessToken(json.getString("access_token"));
            twitterDto.setRefreshToken(json.getString("refresh_token"));
            twitterDto.setTokenType(json.getString("token_type"));
            twitterDto.setExpiresIn(json.getInteger("expires_in"));
            twitterDto.setScope(json.getString("scope"));
            log.info("twitterDto: {}", GsonUtil.parseObject(twitterDto));
            return twitterDto;
        } catch (Exception e) {
            log.error("requestBearerToken error", e);
            return null;
        }
    }

    /**
     * 根据用户token换取用户信息
     *
     * @return
     */
    public TwitterUserDto getUserInfoByToken(String token) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            // Twitter API endpoint
            String endpoint = "https://api.twitter.com/2/users/me";
            // 构造带有参数的 URL
            String urlWithParams = endpoint + "?user.fields=name,pinned_tweet_id,profile_image_url";
            // 创建 URL 对象
            URL url = new URL(urlWithParams);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            TwitterUserDto dto = new TwitterUserDto();
            JSONObject json = JSONObject.parseObject(result.toString());
            JSONObject user = (JSONObject) json.get("data");
            if (user != null) {
                dto.setTwitterUserId(user.get("id").toString());
                dto.setTwitterUserName(user.get("name").toString());
                dto.setTwitterScreenName(user.get("username").toString());
            }
            return dto;
        } catch (Exception e) {
            log.error("getUserInfoByToken error", e);
        }
        return null;
    }
}
