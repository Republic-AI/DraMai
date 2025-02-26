package com.infinity.register;

import java.util.List;

public interface IRegisterData {
    boolean check();

    List<NodeConfig> getAllConfig(String nodeId);

    boolean register(NodeConfig config);

    boolean remove(String nodeId);

    void exit();
}
