package com.infinity.network.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.infinity.network.Constant;
import com.infinity.network.IChannel;
import com.infinity.network.ManagerService;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.MessageOuterClass;
import com.infinity.task.ITask;
import com.infinity.task.ITaskFactory;
import com.infinity.task.ITaskManager;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class LengthCodec implements ICodec {
    public LengthCodec() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void push(ByteBuffer packet, IChannel channel) {
        decodeToTask(packet, channel);
    }

    private void decodeToTask(ByteBuffer packet, IChannel channel) {
        if (packet == null) {
            return;
        }

        int totalLength = packet.remaining();
        if (totalLength <= 0 || totalLength >= Constant.kMaxPacketLength) {
            log.error("link[{},{}] totalLength[{}] is invalid", channel.getID(), channel.getNodeID(),totalLength);
            return;
        }

        int headerLength = packet.getInt();
        if (headerLength <= 0 || headerLength > totalLength) {
            log.error("link[{},{}] headerLength[{}],totalLength[{}] is invalid", channel.getID(), channel.getNodeID(),
                    headerLength,totalLength);
            return;
        }

        byte[] header = new byte[headerLength];
        try {
            packet.get(header);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 创建任务
            HeaderOuterClass.Header newHeader = HeaderOuterClass.Header.parseFrom(header);
            if (newHeader != null) {


                ByteBuffer remainingBuffer = packet;
                int length = remainingBuffer.getInt();
                if (length > remainingBuffer.remaining()) {
                    log.error("failed to parse register node task. the protocol parse error. length={},remaining={}",
                            length, remainingBuffer.remaining());
                    return;
                }

                byte[] regRawData = new byte[length];
                remainingBuffer.get(regRawData);

                MessageOuterClass.Message message = MessageOuterClass.Message.parseFrom(regRawData);
                byte[] bytes = message.getContent().toByteArray();
                /*log.debug("command=" + newHeader.getCommand() + ",source=" + newHeader.getSource()
                        + ", dest=" + newHeader.getDestination());*/

                ITaskFactory taskFactory = ManagerService.getTaskFactory();
                if (taskFactory != null) {
                    ITask newITask = taskFactory.createTask(newHeader.getCommand(),
                            newHeader, bytes, channel, null);
                    if (newITask != null) {
                        ITaskManager taskManager = ManagerService.getTaskManager();
                        taskManager.add(newITask);
                    }
                }
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("failed to create task. msg={}", e.getMessage());
            e.printStackTrace();
        }
    }
}