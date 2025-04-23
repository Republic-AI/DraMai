package com.infinity.ai.platform.npc.character;

import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.map.Position;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.event.EventType;
import com.infinity.ai.platform.npc.goap.action.*;

import java.util.*;

//农夫NPC
public class KaiNPC extends NPC {

    public KaiNPC(Long id, String name) {
        super(id, name);
    }

    @Override
    protected void init() {
        //启动增加当前正在做的行为
        super.initAction();
        /*this.oidList.add("zhongbencongType");
        this.oidList.add("zhongbencongThink");
        this.oidList.add("zhongbencongFix");
        this.oidList.add("zhongbencongRead");
        this.oidList.add("popcatBuy");
        this.oidList.add("satoshiDricoffee");*/
        //this.oidList.add("moveTo");
        //this.oidList.add("moveTo");
        //this.oidList.add("elizaTree");
        //sthis.oidList.add("share");
        this.oidList.add("check");
    }

    //注册NPC行为
    protected void registAction() {
        this.addAction(new MoveToAction(null));
        this.addAction(new SpeakAction(null));
        this.addAction(new ReplyChatAction(null));
        this.addAction(new BuyAction(null, null, Map.of("10007", "popcatBuy", "10008", "pepeBuy")));
        this.addAction(new TalkAction(null));
        this.addAction(new DrinkCoffeeAction(null));
        this.addAction(new GotoNpcAction(null));
        this.addAction(new ChristmasTreeAction(null));
        this.addAction(new SpeachAction(null));
        this.addAction(new MeetingAction(null));
        this.addAction(new DinningAction(null));
        this.addAction(new CookAction(null));
        this.addAction(new SleepAction(null));
        this.addAction(new ThinkAction(null));
        this.addAction(new ReadAction(null));
        this.addAction(new ShareAction(null));
        this.addAction(new CheckAction(null, List.of("trumpCheck_down_1", "trumpCheck_left_2", "trumpCheck_up_3")));
        this.addAction(new DataAnalysisAction(null));
        this.addAction(new ShareAction(null));
    }

    @Override
    protected CharacterType npcType() {
        return CharacterType.Kai;
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
                //params.put("oid", oid);
               // doAction(ActionEnumType.Move.getCode(), params);
                Map<String, Object> params = new HashMap<>();
                params.put("oid", oid);
                int actionId = 0;
                if (oid.equals("check")) {
                    actionId = ActionEnumType.Check.getCode();
                }  else {
                    actionId = ActionEnumType.Read.getCode();
                }
                doAction(actionId, params, true);
            }
            index++;
        }
    }
}
