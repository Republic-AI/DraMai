package com.infinity.ai.domain.tables;

import lombok.Data;

/**
 * 对应player表
 */
@Data
public class VPlayer {
    private PlayerBase base = new PlayerBase();
    private PlayerTimes times = new PlayerTimes();
    private PlayerBag bag = new PlayerBag();
    private PlayerTask task = new PlayerTask();
    private PlayerSign sign = new PlayerSign();
    private PlayerShop shop = new PlayerShop();
    private PlayerNpc npc = new PlayerNpc();
    private PlayerLive live = new PlayerLive();
    private PlayerTwitter playerTwitter = new PlayerTwitter();
}
