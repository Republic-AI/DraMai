package com.infinity.ai.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class BotCmd {

    //@Autowired
    private BotCmdContext context;

    //@PostConstruct
    private void init() {
        context.register(cmdType().name(), this);
        afterInit(context);
    }

    protected abstract CmdEnum cmdType();

    protected abstract void afterInit(BotCmdContext context);

    protected abstract void received(Update update, AbsSender bot);
}
