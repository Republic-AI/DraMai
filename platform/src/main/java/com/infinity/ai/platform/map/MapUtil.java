package com.infinity.ai.platform.map;


public class MapUtil {

    public static int getDistance(Position position1, Position position2) {
        return (int) Math.sqrt(Math.pow(position1.x - position2.x, 2) + Math.pow(position1.y - position2.y, 2));
    }

    public static int getGridKey(int gridX, int gridY) {
       return gridX << 16 | gridY;
    }
}
