package com.infinity.ai.platform.constant;

import lombok.Getter;

public enum NFTType {
    avacabinet(0, "avacabinet"),
    muskrocket(1, "muskrocket"),
    pepetruck(2, "pepetruck"),
    pippinswing(3, "pippinswing"),
    popcatfrog(4, "popcatfrog"),
    satoshibooks(5, "satoshibooks"),
    trumpflag(6, "trumpflag"),
    lunamoon(7, "lunamoon"),
    ;

    @Getter
    private int id;
    @Getter
    private String name;

    NFTType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
