package com.infinity.ai.platform.map.object;

import com.infinity.ai.platform.map.MapUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class MapGrid {

    private int gridX;

    private int gridY;

    private int key;

    private List<MapElement> elements;

    public MapGrid(int gridX, int girdY) {
        this.gridX = gridX;
        this.gridY = girdY;
        this.key = MapUtil.getGridKey(gridX, gridY);
        this.elements = new ArrayList<>();
    }

    public boolean addElement(MapElement element) {
        elements.add(element);
        return true;
    }

    public void removeElemet(MapElement element) {
        Iterator<MapElement> mapElementIterator = elements.iterator();
        MapElement mapElement;
        while (mapElementIterator.hasNext()) {
            mapElement = mapElementIterator.next();
            if (mapElement.equals(element)) {
                mapElementIterator.remove();
            }
        }
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean canStand(MapElement mapElement) {
        for (MapElement mapElement1 : elements) {
            if (mapElement.getElementType() == mapElement1.getElementType()) {
                return false;
            }
        }
        return true;
    }
}
