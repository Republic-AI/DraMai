package com.infinity.ai.platform.manager;

import com.infinity.ai.PNpc;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.data.PropertyListener;
import com.infinity.ai.platform.npc.data.WorldDataListener;
import com.infinity.db.db.DBManager;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NpcManager {
    //当前在线的NPC
    private final Map<Long, NpcHolder> onlineNPC = new ConcurrentHashMap<>();
    //NPC集合
    private Map<Long, NPC> npcMap = new ConcurrentHashMap<>();
    //NPC模板集合
    @Getter
    private Map<Integer, Long> npcTemplateMap = new ConcurrentHashMap<>();
    @Getter
    private final PropertyListener listener;
    @Getter
    private final WorldDataListener worldDataListener;
    /*@Getter
    private final EventManager eventManager;*/

    private boolean exit = false;

    public NpcManager(){
        listener = new PropertyListener();
        worldDataListener = new WorldDataListener();
        worldDataListener.addPropertyChangeListener(listener);
        //eventManager = new EventManager();
    }

    public void dispose() {
        exit = true;
    }

    public void addNPC(NPC npc) {
        npcMap.putIfAbsent(npc.getId(), npc);
    }

    public boolean hasNpcWithID(final long npcId) {
        NpcHolder onlineNpcHolder = this.getOnlineNpcHolder(npcId);
        if (onlineNpcHolder == null) {
            PNpc p = DBManager.get(PNpc.class, npcId);
            return p != null;
        }

        return true;
    }

    public NpcHolder getOnlineNpcHolder(final long npcId) {
        NpcHolder npcHolder = onlineNPC.get(npcId);
        return npcHolder;
    }

    public NpcHolder addOnlineNpc(final long npcId, final PNpc dbNpc) {
        NpcHolder onlineNpcHolder = this.getOnlineNpcHolder(npcId);
        if (onlineNpcHolder != null) {
            return onlineNpcHolder;
        }

        NpcHolder newOnlineNpc = new NpcHolder(npcId, dbNpc);
        onlineNPC.put(npcId, newOnlineNpc);
        return newOnlineNpc;
    }

    public void removeOnlineNpc(final long npcId) {
        NpcHolder npcHolder = onlineNPC.remove(npcId);
        NPC npc = npcMap.remove(npcId);
        if(npc != null){
            npc.setExit(true);
        }

        if (npcHolder != null) {
            npcHolder.dispose();
        }
    }

    // 仅仅将一个玩家移出 管理器列表,不需要做存盘处理
    public final void justRemove(long npcId) {
        onlineNPC.remove(npcId);
    }

    public Map<Long, NpcHolder> getOnlineNpcMap() {
        return onlineNPC;
    }

    /**
     * 获取一个玩家,忽略是否在线
     *
     * @return
     */
    public NpcHolder getNpcHolderIgnoreOnline(long npcId) {
        NpcHolder npcHolder = this.getOnlineNpcHolder(npcId);
        if (npcHolder == null) {
            // 不在线,查看是否有这个玩家
            PNpc npc = DBManager.get(PNpc.class, npcId);
            if (npc == null) {
                return null;
            }

            npcHolder = new NpcHolder(npcId);
        }
        return npcHolder;
    }

    private static class Holder {
        private static final NpcManager kInstance = new NpcManager();
    }

    public static NpcManager getInstance() {
        return NpcManager.Holder.kInstance;
    }
}
