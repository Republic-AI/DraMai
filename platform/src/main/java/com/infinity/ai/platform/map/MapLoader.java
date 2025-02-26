package com.infinity.ai.platform.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.ai.platform.map.event.MapEvent;
import com.infinity.ai.platform.map.event.NearEvent;
import com.infinity.ai.platform.map.object.MapData;
import com.infinity.ai.platform.map.object.MapObject;
import com.infinity.ai.platform.map.object.ObjectStatus;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class MapLoader {

    public static int[][] parseMap(String filePath) {
        try {
            log.info("start parse Map x.y data......");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            doc.getDocumentElement().normalize();
            NodeList dataList = doc.getElementsByTagName("data");

            if (dataList.getLength() == 0) {
                throw new Exception("No data element found in the XML file");
            }
            Element dataElement = null;
            for (int i = 0; i < dataList.getLength(); i++) {
                dataElement = (Element) dataList.item(i);
                if (((Element)dataElement.getParentNode()).getAttribute("name").equals("building")) {
                    break;
                }
            }

            String dataContent = dataElement.getTextContent().trim();

            // Split the data content into rows
            String[] rows = dataContent.split("\\s+");

            // Initialize the 2D array with the size of the map
            int rowCount = rows.length;
            int colCount = rows[0].split(",").length;
            int[][] map = new int[colCount][rowCount];

            // Parse each row into the 2D array
            for (int i = 0; i < rowCount; i++) {
                String[] cols = rows[i].split(",");
                for (int j = 0; j < colCount; j++) {
                    map[j][i] = Integer.parseInt(cols[j]);
                }
            }

            log.info("parse Map x.y data done......");
            return map;
        } catch (Exception e) {
            log.info("parse Map x.y data error......");
            System.exit(-1);
            e.printStackTrace();
        }
        return null;
    }

    public static MapData mapParser(String mapFile) throws IOException {
        MapData data = new MapData();
        // 创建 ObjectMapper 对象
        ObjectMapper mapper = new ObjectMapper();
        // 读取 JSON 文件
        JsonNode rootNode = mapper.readTree(new File(mapFile));

        //地图名称和宽高
        data.setName(rootNode.path("name").asText());
        data.setWidth(rootNode.path("width").asInt());
        data.setHeight(rootNode.path("height").asInt());

        // 解析 objectgroup 对象
        JsonNode objectGroup = rootNode.path("objectgroup").path("object");
        // 用于存储 linkNpcId 和 type 相同的对象
        Map<Integer, Map<String, List<MapObject>>> groupedObjects = new HashMap<>();

        MapEvent mapEvent = createMapEvent();

        // 遍历所有对象
        for (JsonNode node : objectGroup) {
            MapObject obj = new MapObject();
            obj.id = node.path("id").asText();
            obj.name = node.path("name").asText();
            obj.type = node.path("type").asText();
            obj.x = node.path("x").asInt();
            obj.y = node.path("y").asInt();
            obj.width = node.path("width").asInt();
            obj.height = node.path("height").asInt();
            obj.properties = new HashMap<>();
            obj.setEvents(Arrays.asList(mapEvent));
            obj.setState(ObjectStatus.createState(obj.type,0));

            // 解析 properties 属性
            for (JsonNode propNode : node.path("properties").path("property")) {
                String propName = propNode.path("name").asText();
                String propValue = propNode.path("value").asText();
                obj.properties.put(propName, propValue);
            }

            // 获取 linkNpcId 属性值
            /*int linkNpcId = Integer.parseInt(obj.properties.get("linkNpcId").toString());

            // 根据 linkNpcId 和 type 将对象分组
            groupedObjects.computeIfAbsent(linkNpcId, k -> new HashMap<>())
                    .computeIfAbsent(obj.type, k -> new ArrayList<>())
                    .add(obj);*/

            data.addObj(obj);
        }

        data.setObjectgroup(groupedObjects);
        // 输出分组结果
        /*for (Map.Entry<Integer, Map<String, List<MapObject>>> entry : groupedObjects.entrySet()) {
            System.out.println("linkNpcId: " + entry.getKey());
            for (Map.Entry<String, List<MapObject>> typeEntry : entry.getValue().entrySet()) {
                System.out.println("  type: " + typeEntry.getKey());
                for (MapObject obj : typeEntry.getValue()) {
                    System.out.println("    " + obj);
                }
            }
            System.out.println();
        }*/
        return data;
    }

    private static MapEvent createMapEvent() {
        return new NearEvent();
    }

}