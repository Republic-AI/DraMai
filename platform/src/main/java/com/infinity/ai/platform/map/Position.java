package com.infinity.ai.platform.map;

import lombok.Data;

import java.util.Objects;

// 定义节点类
@Data
public class Position implements Comparable<Position> {
    public int x, y; // 节点的坐标
    public transient int g, h, f; // g是从起点到当前节点的代价，h是启发式估计值，f = g + h
    public Position parent; // 当前节点的父节点，用于构建路径

    // 节点构造函数
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 比较节点，用于优先队列中的排序
    @Override
    public int compareTo(Position other) {
        return Integer.compare(this.f, other.f);
    }

    // 重写equals方法，用于节点比较
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position node = (Position) obj;
        return x == node.x && y == node.y;
    }

    // 重写hashCode方法，用于在集合中存储节点
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}


