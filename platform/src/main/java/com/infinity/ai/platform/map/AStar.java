package com.infinity.ai.platform.map;

import com.infinity.ai.platform.manager.MapManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.common.consts.GameConsts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

// A*算法实现
@Slf4j
@Data
public class AStar {
    private int[][] map; // 地图表示，0表示可通行，1表示障碍物
    private int width, height; // 地图的宽度和高度

    // A*算法构造函数
    public AStar(int[][] map) {
        this.map = map;
        this.width = map.length;
        this.height = map[0].length;
    }

    // 寻找从起点到终点的路径
    public List<Position> findPath(NPC npc, Position start, Position end) {
        // 优先队列，用于存储待处理节点
        PriorityQueue<Position> openList = new PriorityQueue<>(Comparator.comparingInt(o -> o.f));
        // 集合，用于存储已处理节点
        Set<Position> closedList = new HashSet<>();

        // 初始化起点节点
        start.g = 0; // 起点到起点的代价为0
        start.h = heuristic(start, end); // 计算起点到终点的启发式估计值
        start.f = start.g + start.h; // 计算起点的总代价
        openList.offer(start); // 将起点加入优先队列

        while (!openList.isEmpty()) {
            Position current = openList.poll(); // 从优先队列中取出代价最小的节点
            closedList.add(current); // 将当前节点加入已处理集合
            if (current.equals(end)) {
                return constructPath(current); // 如果当前节点是终点，则构建路径并返回
            }
            List<Position> positionList = getNeighbors(current, npc.getId() > GameConsts.MAX_NPC_ID, npc.getRoomId());
            for (Position neighbor : positionList) { // 遍历当前节点的邻居节点
                if (closedList.contains(neighbor)) continue; // 如果邻居节点已经处理过，则跳过

                int tentativeG = current.g + 1; // 假设移动成本为1
                if (!openList.contains(neighbor) || tentativeG < neighbor.g) {
                    neighbor.g = tentativeG; // 更新邻居节点的代价
                    neighbor.h = heuristic(neighbor, end); // 重新计算启发式估计值
                    neighbor.f = neighbor.g + neighbor.h; // 重新计算总代价
                    neighbor.parent = current; // 更新邻居节点的父节点

                    if (!openList.contains(neighbor)) {
                        openList.offer(neighbor); // 将邻居节点加入优先队列
                    }
                }
            }
        }
        return Collections.emptyList(); // 没有找到路径，返回空列表
    }

    // 获取邻居节点
    public List<Position> getNeighbors(Position node, boolean checkEmpty, int roomId) {
        List<Position> neighbors = new ArrayList<>();
        // 四个方向，分别为上、右、下、左
        int[][] directions = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            // 检查新的坐标是否在地图范围内且不是障碍物
            if (newX >= 0 && newX < width && newY >= 0 && newY < height && map[newX][newY] == 0) {
                if (!checkEmpty || RoomManager.getInstance().getMapManager(roomId).isEmpty(newX, newY)) {
                    neighbors.add(new Position(newX, newY));
                }
            }
        }
        return neighbors;
    }


    // 启发式函数，使用曼哈顿距离
    private int heuristic(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    // 构建路径，从终点回溯到起点
    private List<Position> constructPath(Position node) {
        List<Position> path = new LinkedList<>();
        while (node != null) {
            path.add(0, new Position(node.getX(), node.getY())); // 将节点添加到路径的开头
            node = node.parent; // 回溯到父节点
        }
        return path;
    }
}