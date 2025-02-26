package com.infinity.ai.platform.npc.character;

import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.map.Position;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.event.EventType;
import com.infinity.ai.platform.npc.goap.action.*;

import java.util.*;

//农夫NPC
public class PippinNPC extends NPC {

    public PippinNPC(Long id, String name) {
        super(id, name);
    }

    @Override
    protected void init() {
        //启动增加当前正在做的行为
        super.initAction();
        //this.oidList.add("moveTo");
        //this.oidList.add("moveTo");
        /*this.oidList.add("pippinThink");
        this.oidList.add("pippinRead");
        this.oidList.add("pippinCook");
        this.oidList.add("pippinEat");
        this.oidList.add("pippinSleep");
        this.oidList.add("pippinBuy");
        this.oidList.add("pippinSalecoffee");
        this.oidList.add("pippinMakecoffee");*/
        this.oidList.add("share");
    }

    //注册NPC行为
    protected void registAction() {
        this.addAction(new MoveToAction(null));
        this.addAction(new ThinkAction(null));
        this.addAction(new ReadAction(null));
        this.addAction(new SpeakAction(null));
        this.addAction(new ReplyChatAction(null));
        this.addAction(new CookAction(null));
        this.addAction(new DinningAction(List.of(new CookAction(null, List.of("pippinCook")))));
        this.addAction(new SleepAction(null));
        this.addAction(new TalkAction(null));
        this.addAction(new MakeCoffeeAction(null));
        this.addAction(new SaleAction(List.of(new MakeCoffeeAction(null, List.of("pippinMakecoffee")))));
        this.addAction(new BuyAction(null, null, Map.of("10007", "popcatBuy", "10008", "pepeBuy")));
        this.addAction(new GotoNpcAction(null));
        this.addAction(new ChristmasTreeAction(null));
        this.addAction(new ShareAction(null));
    }

    @Override
    protected CharacterType npcType() {
        return CharacterType.Satoshi;
    }

    @Override
    public Set<EventType> register() {
        Set<EventType> objects = new HashSet<>();
        objects.add(EventType.NPC_APPROACH);
        objects.add(EventType.TIME_CHANGE);
        return objects;
    }

    @Override
    protected void initServerAi() {
        if (todo.isEmpty() && current == 0) {
            if (index >= this.oidList.size()) {
                index = 0;
                Collections.shuffle(oidList);
            }
            String oid =  this.oidList.get(index);
            if (oid.equals("moveTo")) {
                Map<String, Object> params = new HashMap<>();
                Position position1 = MapDataManager.getInstance().getGameMap().randomPassablePostion(this.getGridPostion(), 5);
                params.put("gridX", position1.x);
                params.put("gridY", position1.y);
                doAction(ActionEnumType.Move.getCode(), params, true);
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("oid", oid);
                int actionId = 0;
                if (oid.equals("pippinThink")) {
                    actionId = ActionEnumType.Think.getCode();
                } else if (oid.equals("pippinEat")) {
                    actionId = ActionEnumType.Dinning.getCode();
                } else if (oid.equals("pippinRead")){
                    actionId = ActionEnumType.Read.getCode();
                } else if (oid.equals("pippinSleep")) {
                    actionId = ActionEnumType.Sleep.getCode();
                } else if (oid.equals("pippinMakeCoffee")) {
                    actionId = ActionEnumType.MakeCoffee.getCode();
                } else if (oid.equals("pippinBuy")) {
                    params.put("npcId", 10008);
                    actionId = ActionEnumType.Buy.getCode();
                } else if (oid.equals("pippinCook")) {
                    actionId = ActionEnumType.Cook.getCode();
                } else if (oid.equals("share")) {
                    params.remove("oid");
                    params.put("npcId", 10008);
                    actionId = ActionEnumType.Share.getCode();
                }
                doAction(actionId, params, true);
            }
            index++;
        }
    }
}
