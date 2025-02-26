package com.infinity.ai.platform.manager;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.db.cache.CacheManager;
import com.infinity.db.db.DBManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NpcHolder {
    private final long npcId;

    @Getter
    @Setter
    private NPC npc;

    @Getter
    public final NpcBagManager bag;

    @Getter
    private Object lock = new Object();

    public NpcHolder(final long npcId, PNpc npcModel) {
        this(npcId, npcModel, true);
    }

    public NpcHolder(final long npcId) {
        this(npcId, null, false);
    }

    private NpcHolder(long npcId, PNpc npcModel, boolean online) {
        this.npcId = npcId;
        bag = new NpcBagManager(this);
    }

    public final PNpc getNpcModel() {
        return DBManager.get(PNpc.class, npcId);
    }

    public void dispose() {
        PNpc npc = this.getNpcModel();
        String name = this.getName();
        DBManager.update(npc);
        log.info("npc[{},{}] exit", this.npcId, name);
        CacheManager.get().expire(PNpc.class, npcId);
        this.npc.setHolder(null);
        this.npc = null;
    }

    public final long getNpcId() {
        return npcId;
    }

    public final String getName() {
        return this.getNpcModel().getName();
    }

    public boolean online() {
        return NpcManager.getInstance().getOnlineNpcHolder(npcId) != null;
    }
}
