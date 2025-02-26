package com.infinity.common.msg.platform.npc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NFTData {

    private int id;

    private int state;

    private String userNo;

    private String mint;

    private String txSignature;

    private long slot;
}
