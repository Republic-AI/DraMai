package com.infinity.ai.platform.application;

import com.infinity.ai.platform.task.frame.FrameSyncTask;
import com.infinity.ai.platform.task.gm.GmTask;
import com.infinity.ai.platform.task.goods.QueryGoodsTask;
import com.infinity.ai.platform.task.goods.TypeToEarnTask;
import com.infinity.ai.platform.task.live.QueryGiftTask;
import com.infinity.ai.platform.task.live.QueryRankTask;
import com.infinity.ai.platform.task.live.SendGiftTask;
import com.infinity.ai.platform.task.live.SwithLiveTask;
import com.infinity.ai.platform.task.map.QueryMapDataTask;
import com.infinity.ai.platform.task.npc.*;
import com.infinity.ai.platform.task.player.LoginTask;
import com.infinity.ai.platform.task.player.LogoutTask;
import com.infinity.ai.platform.task.player.RefreshGameUserTask;
import com.infinity.ai.platform.task.quartz.EveryDayZeroTimerTask;
import com.infinity.ai.platform.task.sign.SignTask;
import com.infinity.ai.platform.task.system.RefreshTask;
import com.infinity.ai.platform.task.timer.ExpireTask;
import com.infinity.network.IChannel;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.ProtocolCommon;
import com.infinity.task.ITask;
import com.infinity.task.ITaskCreator;
import com.infinity.task.ITaskFactory;
import com.infinity.task.node.HeartbeatTask;
import com.infinity.task.node.NodeRegisterTask;
import com.infinity.task.node.NodeUpdateTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class NewTaskFactory implements ITaskFactory {
    private static Map<Integer, ITaskCreator> creator_;

    /**
     * 在此注册命令任务
     */
    static {
        NewTaskFactory.initCreatorImpl();

        NewTaskFactory.register(ProtocolCommon.kHeartBeatCommand, HeartbeatTask::new);
        NewTaskFactory.register(ProtocolCommon.kUpdateNodeCommand, NodeUpdateTask::new);
        NewTaskFactory.register(ProtocolCommon.kRegisterNodeCommand, NodeRegisterTask::new);

        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.LOGIN_COMMAND, LoginTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.CHARATER_SET_COMMAND, PlayerNpcSetTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.CHARATER_QUERY_COMMAND, QueryPlayerNpcTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.FRAME_SYNC_COMMAND, FrameSyncTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.NPC_ACTION_COMMAND, NpcAtionTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.QUERY_MAP_DATA_COMMAND, QueryMapDataTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.SYNC_NPC_ACTION_COMMAND, SyncDataTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.QUERY_NPC_DATA_COMMAND, QueryNpcDataTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.TYPE_TO_EARN_COMMAND, TypeToEarnTask::new);

        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.SWITH_LIVE_COMMAND, SwithLiveTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.QUERY_GIFT_COMMAND, QueryGiftTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.SEND_GIFT_COMMAND, SendGiftTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.QUERY_RANK_COMMAND, QueryRankTask::new);


        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.OFF_LINE_COMMAND, LogoutTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.MSG_CODE_REFRESH_GAMEUSER, RefreshGameUserTask::new);

        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.kGoodsSelectCommand, QueryGoodsTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.kSignCommand, SignTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.MSG_CODE_TIMER_EVERYDAYZERO, EveryDayZeroTimerTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.kGmCommand, GmTask::new);

        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.MSG_CODE_TIMER_SUBMIT, ExpireTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.SYS_REFRESH_COMMAND, RefreshTask::new);

        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.CHAT_HISTORY_COMMAND, QueryNpcChatTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.PLAYER_MOVE_COMMAND, PlayerNpcMoveTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.PLAYER_SPEAK_COMMAND, PlayerNpcSpeakTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.PLAYER_NFT_COMMAND, PlayerNFTTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.QUERY_NFT_COMMAND, QueryNFTInfoTask::new);

        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.P_NPC_TALK_COMMAND, ChatNpcTask::new);
        NewTaskFactory.register(com.infinity.common.msg.ProtocolCommon.NPC_CHANGE_COMMAND, NpcChangeTask::new);
        log.info("total commands: {}", creator_.size());
    }

    public static void initCreatorImpl() {
        if (NewTaskFactory.creator_ == null)
            NewTaskFactory.creator_ = new ConcurrentHashMap<>();
    }

    public static void finiCreatorImp() {
        if (NewTaskFactory.creator_ != null)
            NewTaskFactory.creator_.clear();
        NewTaskFactory.creator_ = null;
    }

    private static class Creator<T extends ITask> implements ITaskCreator {
        private final int command_;
        private final Supplier<T> creator_impl_;

        public Creator(final int command, final Supplier<T> creatorImpl) {
            this.command_ = command;
            this.creator_impl_ = creatorImpl;
        }

        @Override
        public int command() {
            return this.command_;
        }

        @Override
        public ITask create() {
            return this.creator_impl_.get();
        }
    }

    public static <T extends ITask> void register(final int command, final Supplier<T> taskCreator) {
        assert NewTaskFactory.creator_ != null;
        if (NewTaskFactory.creator_.putIfAbsent(command, new Creator<>(command, taskCreator)) != null)
            throw new IllegalArgumentException("repeat command " + command);
    }

    @Override
    public void dispose() {
        assert NewTaskFactory.creator_ != null;
        NewTaskFactory.finiCreatorImp();
    }

    @Override
    public ITask createTask(int command, HeaderOuterClass.Header header, byte[] buffer, IChannel channel, Object attachement) {
        assert NewTaskFactory.creator_ != null;
        ITaskCreator creator = NewTaskFactory.creator_.get(command);
        if (creator != null) {
            try {
                ITask newTask = creator.create();
                if (newTask != null) {
                    newTask.setChannel(channel);
                    newTask.setExtras(buffer);
                    newTask.setHeader(header);
                    newTask.setAttachment(attachement);
                    newTask.init();

                    return newTask;
                }
            } catch (Exception exception) {
                log.error("failed to create task.cmd={},header={},msg={}",
                        command, header.toString(), exception.getMessage());
                exception.printStackTrace();
            }
        } else {
            log.error("failed to creat task. cmd={},header={}", command, header.toString());
        }
        return null;
    }
}
