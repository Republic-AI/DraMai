package com.infinity.ai.platform.npc.character;

import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.map.Position;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.event.EventType;
import com.infinity.ai.platform.npc.goap.action.*;

import java.util.*;

//农夫NPC
public class AvaNPC extends NPC {

    public AvaNPC(Long id, String name) {
        super(id, name);
    }

    @Override
    protected void init() {
        //启动增加当前正在做的行为
        super.initAction();
        //this.oidList.add("moveTo");
        //this.oidList.add("moveTo");
        //this.oidList.add("pepeBuy");
        //this.oidList.add("muskThink");
        //this.oidList.add("muskEat");
        //this.oidList.add("muskRead");
        //this.oidList.add("muskMeeting");
        //this.oidList.add("muskData");
        //this.oidList.add("muskDricoffee");
        //this.oidList.add("share");
        this.oidList.add("avaCheck");
    }

    //注册NPC行为
    protected void registAction() {
        this.addAction(new MoveToAction(null));
        this.addAction(new ThinkAction(null));
        this.addAction(new TypeAction(null));
        this.addAction(new RepairAction(null));
        this.addAction(new ReadAction(null));
        this.addAction(new SpeakAction(null));
        this.addAction(new ReplyChatAction(null));
        this.addAction(new CookAction(null));
        this.addAction(new DinningAction(List.of(new CookAction(null, List.of("muskCook")))));
        this.addAction(new MeetingAction(null));
        this.addAction(new DataAnalysisAction(null, List.of("muskData_left_1")));
        this.addAction(new SleepAction(null));
        this.addAction(new TalkAction(null));
        this.addAction(new DrinkCoffeeAction(null));
        this.addAction(new BuyAction(null, List.of("pepeBuy")));
        this.addAction(new GotoNpcAction(null));
        this.addAction(new ChristmasTreeAction(null));
        this.addAction(new GameAction(null));
        this.addAction(new ShareAction(null));
        this.addAction(new CheckAction(null, List.of("avaCheck_left_1", "avaCheck_down_2", "avaCheck_up_3")));
    }

    @Override
    protected CharacterType npcType() {
        return CharacterType.Ava;
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
                if (oid.equals("avaCheck")) {
                    actionId = ActionEnumType.Check.getCode();
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
