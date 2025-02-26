package com.infinity.ai.platform.npc.goap.action;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

// 打字行动类
@Slf4j
public class DrinkCoffeeAction extends CommonAction {

    public DrinkCoffeeAction(List<Action> preActions, List<String> oids) {
        super(preActions, oids);
    }

    public DrinkCoffeeAction(List<Action> preActions) {
        super(preActions);
        this.oids = List.of("pippinMakeCoffee","muskDricoffee","pepeDricoffee","popcatDricoffee","satoshiDricoffee","elizaDricoffee","trumpDricoffee","morpheusDricoffee");
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.DrinkCoffee;
    }
}


