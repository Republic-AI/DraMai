package com.infinity.ai.telegram.controller;

import com.infinity.ai.telegram.common.Constant;
import com.infinity.ai.telegram.common.SecretUtitls;
import com.infinity.ai.telegram.service.UserService;
import com.infinity.ai.telegram.bot.CatWebhookBot;
import com.infinity.common.base.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@Slf4j
public class TelegramController {
    @Autowired
    private CatWebhookBot catWebhookBot;
    @Autowired
    private UserService userService;

    @ResponseBody
    @PostMapping("/binding")
    public Response<Boolean> pushBinding(@RequestBody Map<String, String> params) {
        String type = params.get("type");
        //绑定
        if ("0".equals(type)) {
            String tgid = params.get("tgid");
            if (StringUtils.isEmpty(tgid)) {
                Response.createError("tgid 为空");
            }

            String decrypt = SecretUtitls.decrypt(tgid);
            String chatId = decrypt.replaceAll("TG", "");

            catWebhookBot.sendBindingMessage(Long.valueOf(chatId), Constant.WALLET_IMG);
        } else if ("1".equals(type)) {//绑定成功

        }
        return Response.createSuccess(true);
    }

    /**
     * 跳转到游戏页面
     *
     * @param tid TG ID
     * @return 游戏页面地址
     */
    @GetMapping("/play/{tid}")
    public RedirectView playGame(@PathVariable String tid, HttpServletResponse response) {
        log.debug("/play/{}", tid);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        return new RedirectView(catWebhookBot.playGameProcess(tid));
    }

    /**
     * 跳转到游戏绑定钱包页面
     *
     * @param tid TG ID
     * @return 游戏页面地址
     */
    @GetMapping("/bind/{tid}")
    public RedirectView bind(@PathVariable String tid) {
        log.debug("/bind/{}", tid);
        return new RedirectView(catWebhookBot.bindWalletProcess(tid));
    }

    @ResponseBody
    @GetMapping("/push")
    public String publish() {
        userService.push(catWebhookBot);
        return "推送成功";
    }
}
