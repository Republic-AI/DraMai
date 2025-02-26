package com.infinity.register;

import com.alibaba.fastjson.JSON;
import com.infinity.common.cache.lv2.ObjectCacheRedisService;
import com.infinity.common.utils.StringUtils;
import com.infinity.manager.node.NodeManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RedisRegister implements IRegisterData {
    private Map<String, NodeConfig> nodeConfigMap = new ConcurrentHashMap();
    private final String KEY_PREFIX = "cluster";
    private boolean exit_ = false;

    public RedisRegister() {
        init();
    }

    private void init() {
        //60秒重连
        /*Threads.addListener(ThreadConst.TIMER_1S, "register#LoopChecker", new IntervalTimer(1, 60000) {
            @Override
            public boolean exec0(int interval) {
                return ManagerService.getRegister().check();
            }
        });*/
    }

    @Override
    public boolean check() {
        log.debug("RedisRegister check execute.......");
        if (exit_)
            return true;
        NodeManager.getInstance().connect();
        return false;
    }

    @Override
    public List<NodeConfig> getAllConfig(String nodeId) {
        Map<String, String> cfgMap = ObjectCacheRedisService.hgetAll(getKey(nodeId));
        if (cfgMap == null || cfgMap.size() == 0) {
            return new ArrayList<>(nodeConfigMap.values());
        }

        List<NodeConfig> list = new ArrayList<>();
        cfgMap.forEach((k, v) -> {
            NodeConfig nodeConfig = JSON.parseObject(v, NodeConfig.class);
            list.add(nodeConfig);
            nodeConfigMap.put(nodeConfig.getNodeId(), nodeConfig);
        });

        return list;
    }

    @Override
    public boolean register(NodeConfig config) {
        try {
            String nodeId = config.getNodeId();
            if (StringUtils.isEmpty(nodeId)) {
                log.error("RedisRegister register method fail, nodeId is null");
                return false;
            }

            String key = getKey(nodeId);

            //Map类型：key=NodeId, value=config
            nodeConfigMap.put(nodeId, config);
            ObjectCacheRedisService.hset(key, nodeId, JSON.toJSONString(config));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(String nodeId) {
        nodeConfigMap.remove(nodeId);
        return ObjectCacheRedisService.hdel(getKey(nodeId), new String[]{nodeId});
    }

    @Override
    public void exit() {
        exit_ = true;
    }

    private String getKey(String nodeId) {
        String nodePrefix = nodeId.substring(0, nodeId.lastIndexOf("."));
        return KEY_PREFIX + ":" + nodePrefix;
    }

}
