package com.infinity.ai.platform.npc.character;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

//NPC角色类型
@Getter
public enum CharacterType {
    Player(0, "Player"),
    Farmer(1, "Farmer"),
    Grocer(2, "Grocer"),
    Herdman(3, "Herdman"),
    Baker(4, "Baker"),
    Satoshi(5, "Satoshi"),
    Popcat(6, "Popcat"),
    Pepe(7, "Pepe"),
    Musk(8, "Musk"),
    Pippin(9, "Pippin"),
    Eliza(10, "Eliza"),
    Trump(11, "Trump"),
    Morpheos(12, "Morpheos"),
    Ava(13, "Ava"),
    Luna(14, "Luna"),
    Leo(15, "Leo"),
    Ivy(16, "Ivy"),
    Kai(17, "Kai"),
    Selena(18, "Selena"),
    Nova(19, "Nova"),
    Aiden(20, "Aiden"),
    Altman(21, "Altman");


    private final int code;
    private final String name;

    CharacterType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<CharacterType> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }
}
