package com.infinity.ai.platform.npc.goap;

import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.Action;

import java.util.*;

// GOAPPlanner 类用于生成行动计划
public class GOAPPlanner {

    // 生成行动计划
    public static List<Action> plan(NPC npc, Goal goal) {
        List<Action> plan = new ArrayList<>();
        Map<String, Boolean> currentState = new HashMap<>(npc.getWorldState());
        Set<Map<String, Boolean>> visitedStates = new HashSet<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(Node::getPriority));

        frontier.add(new Node(currentState, new ArrayList<>(), 0, 0));

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            Map<String, Boolean> state = node.getState();
            List<Action> actions = node.getActions();

            if (isGoalAchieved(goal, state)) {
                return actions;
            }

            if (visitedStates.contains(state)) {
                continue;
            }
            visitedStates.add(state);

            for (Action action : npc.getActions().values()) {
                if (action.checkPreconditions(state)) {
                    Map<String, Boolean> newState = new HashMap<>(state);
                    newState.putAll(action.getEffects());

                    List<Action> newActions = new ArrayList<>(actions);
                    newActions.add(action);

                    int newCost = node.getCost() + action.getCost();
                    int newDepth = node.getDepth() + 1;

                    frontier.add(new Node(newState, newActions, newCost, newDepth));
                }
            }
        }

        return Collections.emptyList();
    }

    // 检查目标是否已经达成
    private static boolean isGoalAchieved(Goal goal, Map<String, Boolean> state) {
        for (Map.Entry<String, Boolean> condition : goal.getConditions().entrySet()) {
            if (!state.getOrDefault(condition.getKey(), false).equals(condition.getValue())) {
                return false;
            }
        }
        return true;
    }

    // 用于表示搜索树节点的内部类
    private static class Node {
        private final Map<String, Boolean> state;
        private final List<Action> actions;
        private final int cost;
        private final int depth;

        public Node(Map<String, Boolean> state, List<Action> actions, int cost, int depth) {
            this.state = state;
            this.actions = actions;
            this.cost = cost;
            this.depth = depth;
        }

        public Map<String, Boolean> getState() {
            return state;
        }

        public List<Action> getActions() {
            return actions;
        }

        public int getCost() {
            return cost;
        }

        public int getDepth() {
            return depth;
        }

        public int getPriority() {
            return cost * 1000 + depth; // 优先考虑成本，其次考虑路径长度
        }
    }
}


