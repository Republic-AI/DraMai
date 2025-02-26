package com.infinity.ai.platform.dropsystem;

import com.infinity.common.config.data.DropsCfg;

import java.util.List;

public interface IDropComponent {
    void init(final DropsCfg dropsCfg);

    void execute();

    void dispose();

    List<DropAward> getResult();
}
