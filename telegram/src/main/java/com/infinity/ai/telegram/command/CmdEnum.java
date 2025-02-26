package com.infinity.ai.telegram.command;

public enum CmdEnum {
    PLAY_GAME_BTN("\uD83D\uDD79 Play game"),
    FOLLOW_US_BTN("\uD83C\uDF81 Follow us"),
    HELP_BTN("\uD83D\uDCD6 Help"),
    INVITE_BTN("\uD83C\uDF89 Invite"),

    START_CMD("/start"),
    HELP_CMD("/help"),
    FOLLOW_US_CMD("/follow us"),
    INVITE_CMD("/invite"),
    PLAY_CMD("/play"),

    ;

    private final String name;

    CmdEnum( String name) {
        this.name = name;
    }
}
