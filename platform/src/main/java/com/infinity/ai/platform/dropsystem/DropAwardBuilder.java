package com.infinity.ai.platform.dropsystem;

import com.infinity.common.config.data.DropsCfg;
import com.infinity.common.config.manager.DropBaseDataManager;
import com.infinity.common.config.manager.GameConfigManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DropAwardBuilder {
    private final long playerId_;
    private final int chapterId_;
    private final int dungeonId_;
    private final DropBaseDataManager dropManager_;

    private List<DropAward> dropAwards_;

    public DropAwardBuilder(final long playerID,
                            final int chapterID,
                            final int dungeonID) {
        this.playerId_ = playerID;
        this.chapterId_ = chapterID;
        this.dungeonId_ = dungeonID;
        this.dropManager_ = GameConfigManager.getInstance().getDropBaseData();
        assert this.dropManager_ != null : "failed to get drop base data.";
    }

    public void dropItem(final int dropIndex) {
        final DropsCfg dropsCfg = this.dropManager_.getDropsConfigWithID(dropIndex);
        if (dropsCfg == null) {
            log.error("failed to get drop config.dropIndex={}", dropIndex);
            return;
        }

        // FIXED: 填充发放逻辑,根据工厂模式+策略模式来配合
        final int dropType = dropsCfg.getDropType();
        log.debug("drop the item. pid={},cid={},did={},type={},id={},probability={},num={},weight={}",
                this.playerId_,
                this.chapterId_,
                this.dungeonId_,
                dropType,
                dropsCfg.getId(),
                dropsCfg.getDrop_probability(),
                dropsCfg.getDrop_num(),
                dropsCfg.getItemWeight().toString());

        final IDropComponent dropComponent = DropFactory.getInstance().create(dropType);
        if (dropComponent == null) {
            log.error("failed to get drop component. dropIndex={}, dropType={}", dropIndex, dropType);
            return;
        }

        dropComponent.init(dropsCfg);
        dropComponent.execute();
        if (dropAwards_ == null)
            dropAwards_ = dropComponent.getResult();
        else
            dropAwards_.addAll(dropComponent.getResult());
        //dropAwards_ = dropComponent.getResult();
        dropComponent.dispose();
    }

    public List<DropAward> getResult() {
        return this.dropAwards_;
    }
}
