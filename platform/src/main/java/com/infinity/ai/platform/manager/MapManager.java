package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.map.MapUtil;
import com.infinity.ai.platform.map.object.MapElement;
import com.infinity.ai.platform.map.object.MapGrid;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapManager {

    @Getter
    private Map<Integer, MapGrid> gridMap;

    public MapManager() {
        this.gridMap = new ConcurrentHashMap<>();
    }

    public void updatePostion(MapElement mapElement, int gridX, int gridY) {
        if (mapElement.getGridX() == gridX && mapElement.getGridY() == gridY) {
            return;
        }
        MapGrid mapGrid = getMapGrid(mapElement.getGridX(), mapElement.getGridY());
        if (mapGrid == null) {
            mapGrid = createMapGrid(mapElement.getGridX(), mapElement.getGridY());
        }
        mapGrid.addElement(mapElement);
        MapGrid mapGridLeave = getMapGrid(gridX, gridY);
        if (mapGridLeave != null) {
            mapGridLeave.removeElemet(mapElement);
            if (mapGridLeave.isEmpty()) {
                gridMap.remove(mapGridLeave.getKey());
            }
        }
    }

    public void removeElement(MapElement mapElement) {
        MapGrid mapGrid = getMapGrid(mapElement.getGridX(), mapElement.getGridY());
        if (mapGrid != null) {
            mapGrid.removeElemet(mapElement);
            if (mapGrid.isEmpty()) {
                gridMap.remove(mapGrid.getKey());
            }
        }
    }

    public void addElement(MapElement mapElement) {
        MapGrid mapGrid = getMapGrid(mapElement.getGridX(), mapElement.getGridY());
        if (mapGrid == null) {
            mapGrid = createMapGrid(mapElement.getGridX(), mapElement.getGridY());
        }
        mapGrid.addElement(mapElement);
    }

    public MapGrid getMapGrid(int gridX, int gridY) {
        return gridMap.get(MapUtil.getGridKey(gridX, gridY));
    }

    public MapGrid createMapGrid(int gridX, int gridY) {
        MapGrid mapGrid = new MapGrid(gridX, gridY);
        gridMap.put(mapGrid.getKey(), mapGrid);
        return mapGrid;
    }

    public boolean canStand(MapElement mapElement, int gridX, int gridY) {
        MapGrid mapGrid = getMapGrid(gridX, gridY);
        if (mapGrid == null) {
            return true;
        }
        return mapGrid.canStand(mapElement);
    }

    public boolean isEmpty(int gridX, int gridY) {
        MapGrid mapGrid = getMapGrid(gridX, gridY);
        if (mapGrid == null) {
            return true;
        }
        return mapGrid.isEmpty();
    }
}
