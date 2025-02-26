package com.infinity.ai.platform.npc.goap.action;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

//NPC 行为类型
@Getter
public enum ActionEnumType {
    Free(99, "free"),
    Farming(100, "farming"),
    Harvest(101, "harvest"),
    Sale(102, "sale"),
    Buy(103, "buy"),
    Cook(104, "cook"),
    Dinning(105, "dinning"),
    Sleep(106, "sleep"),
    Feeding(107, "Feeding"),
    Slaughter(108, "slaughter"),
    Make(109, "make"),
    Speak(110, "Speak"),
    GetUp(111, "getUp"),
    Move(112, "move"),
    Type(113, "type"),
    Repair(114, "repair"),
    Think(115, "think"),
    Read(116, "read"),
    ReplyChat(117, "ReplyChat"),
    Talk(118, "Talk"),
    Fishing(119, "Fishing"),
    Stock(120, "Stock"),
    TidyUp(121, "TidyUp"),
    Restock(122, "Restock"),
    DataAnalysis(123, "DataAnalysis"),
    Meeting(124, "Meeting"),
    MakeCoffee(125, "MakeCoffee"),
    DrinkCoffee(126, "DrinkCoffee"),
    GotoNpc(127, "GotoNpc"),
    ChristmasTree(128, "ChristmasTree"),
    Speach(129, "Speach"),
    Draw(130, "Draw"),
    Game(131, "Game"),
    Sing(132, "Sing"),
    Share(133, "Share");

    /**
     * 100	耕种	ploughSow
     * 101	采收	harvest
     * 102	销售	sale
     * 103	购买	buy
     * 104	做饭	cook
     * 105	吃	dinning
     * 106	睡眠	sleep
     * 107	投喂	Feeding
     * 108	屠宰	slaughter
     * 109	制作	make
     * 110	说话	transport
     * 111	起床	GetUp
     * 112	移动	Move
     * 113  打字 Type
     * 114  修理 Repair
     * 115  思考 Think
     * 116  读书 Read
     * 117  回复 ReplyChat
     * 118  交谈 Talk
     * 119  钓鱼 Fishing
     * 120  进货 stock
     * 121  整理 TidyUp
     * 122  进货整理 Restock
     * 123  分析数据
     * 124  开会
     */

    private final int code;
    private final String name;

    ActionEnumType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Optional<ActionEnumType> getByCode(int code) {
        return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
    }

    public static boolean isSpeak(int code) {
        return ActionEnumType.Speak.code == code;
    }
}
