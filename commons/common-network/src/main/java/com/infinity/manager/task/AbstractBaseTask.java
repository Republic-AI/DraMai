package com.infinity.manager.task;

import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.node.NodeManager;
import com.infinity.network.IChannel;
import com.infinity.network.udp.UdpChannel;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.ProtocolCommon;
import com.infinity.protocol.ResponseOk;
import com.infinity.task.ITask;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Slf4j
public abstract class AbstractBaseTask implements ITask {
    private IChannel socket_channel_;
    private HeaderOuterClass.Header header_;
    private byte[] remaining_buffer_;

    private boolean error_ = false;
    private int error_code_ = 0;
    private String error_message_ = "";
    private long createTm = System.currentTimeMillis();

    protected boolean isUdp() {
        return this.socket_channel_ instanceof UdpChannel;
    }

    @Override
    public long getCreateTm() {
        return this.createTm;
    }

    public static int calcLength(byte[] header, byte[] data) {
        return 12 + header.length + data.length;
    }

    public static ByteBuffer buildBuffer(final int totalLength, byte[] headerData, byte[] responseData) {
        ByteBuffer responseBuffer = ByteBuffer.allocate(totalLength);
        responseBuffer.putInt(totalLength);
        responseBuffer.putInt(headerData.length);
        responseBuffer.put(headerData);
        responseBuffer.putInt(responseData.length);
        responseBuffer.put(responseData);
        responseBuffer.flip();
        return responseBuffer;
    }

    public static ByteBuffer buildPacketBuffer(final byte[] headerData, final byte[] responseData) {
        int packetLength = 8 + headerData.length + responseData.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(packetLength);
        byteBuffer.putInt(headerData.length);
        byteBuffer.put(headerData);
        byteBuffer.putInt(responseData.length);
        byteBuffer.put(responseData);
        byteBuffer.flip();
        return byteBuffer;
    }

    public static byte[] convertByteContent(final ByteBuffer buffer) {
        if (buffer.hasArray())
            return buffer.array();
        else {
            byte[] cmdContent = new byte[buffer.remaining()];
            buffer.get(cmdContent);
            return cmdContent;
        }
    }

    @Override
    public void init() {
    }


    @Override
    public void setChannel(IChannel socketChannel) {
        socket_channel_ = socketChannel;
    }

    @Override
    public void setHeader(HeaderOuterClass.Header header) {
        header_ = header;
    }

    @Override
    public void setExtras(byte[] buffer) {
        remaining_buffer_ = buffer;
    }

    @Override
    public IChannel getChannel() {
        IChannel socketChannel = socket_channel_;
        if (socketChannel == null) {
            socketChannel = NodeManager.getInstance().getNodeWithType(NodeConstant.kGatewayService).getChannel();
        } else if (!socketChannel.availability()) {
            //如果节点的链路通道已经无效了，那么需要获取一个新的通道来进行通信
            String nodeID = socketChannel.getNodeID();
            INode node = NodeManager.getInstance().getNode(nodeID);
            if (node == null) {
                log.error("failed to get node. nodeID={}", nodeID);
                return socketChannel;
            }
            socketChannel = node.getChannel();
            log.debug("broken channel[{}] switch to channel[nodeID={}, channelID={}]", socket_channel_.getID(), socketChannel.getNodeID(), socketChannel.getID());
        }

        return socketChannel;
    }

    @Override
    public HeaderOuterClass.Header getHeader() {
        return header_;
    }

    @Override
    public byte[] getExtras() {
        return remaining_buffer_;
    }

    protected final int calcTotalResponseLength(byte[] header, byte[] content) {
        return calcLength(header, content);
    }

    protected HeaderOuterClass.Header makeHeader(final int errorCode) {
        HeaderOuterClass.Header thisHeader = this.getHeader();
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(this.getCommandID());
        headerBuilder.setSource(thisHeader.getDestination());
        headerBuilder.setDestination(thisHeader.getSource());
        headerBuilder.setRtype(ProtocolCommon.kResponseTag);
        headerBuilder.setCode(errorCode);
        return headerBuilder.build();
    }

    protected HeaderOuterClass.Header makeHeader(final int command, final int errorCode) {
        HeaderOuterClass.Header thisHeader = this.getHeader();
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(command);
        headerBuilder.setSource(thisHeader.getDestination());
        headerBuilder.setDestination(thisHeader.getSource());
        headerBuilder.setRtype(ProtocolCommon.kResponseTag);
        headerBuilder.setCode(errorCode);
        headerBuilder.setCmd(this.getCommandID());
        return headerBuilder.build();
    }

    protected byte[] buildHeader(final int errorCode, final boolean isRequest) {
        return this.buildHeader(errorCode, isRequest, this.getCommandID());
    }

    protected byte[] buildHeader(final int errorCode, final boolean isRequest, final int commandId) {
        HeaderOuterClass.Header thisHeader = this.getHeader();
        return buildHeaderWithNodeID(errorCode, isRequest, commandId, thisHeader.getSource());
    }

    protected byte[] buildHeaderWithNodeID(final int errorCode,
                                           final boolean isRequest,
                                           final int commandID,
                                           final String targetNodeID) {
        HeaderOuterClass.Header thisHeader = this.getHeader();
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(commandID);
        headerBuilder.setSource(thisHeader.getDestination());
        headerBuilder.setDestination(targetNodeID);
        headerBuilder.setRtype(isRequest ? ProtocolCommon.kRequestTag : ProtocolCommon.kResponseTag);
        headerBuilder.setCode(errorCode);
        return headerBuilder.build().toByteArray();
    }

    protected byte[] buildErrorResponse(final long requestId, final int errorCode, final String errorMessage) {
        ResponseOk.response_ok.Builder errorResponseBuilder = ResponseOk.response_ok.newBuilder();
        errorResponseBuilder.setRequestId(requestId);
        errorResponseBuilder.setCode(errorCode);
        errorResponseBuilder.setMessage(errorMessage);
        ResponseOk.response_ok response = errorResponseBuilder.build();
        return response.toByteArray();
    }

    protected ByteBuffer buildResponse(final int totalLength, byte[] headerData, byte[] responseData) {
        return AbstractBaseTask.buildBuffer(totalLength, headerData, responseData);
    }

    protected ByteBuffer makeResponse(final byte[] headerData, final byte[] responseData) {
        return buildPacketBuffer(headerData, responseData);
    }

    protected void sendResponse(ByteBuffer responseData) {
        IChannel channel = getChannel();
        assert (channel != null && responseData != null) : "failed to get channel.";
        channel.write(responseData);
    }

    protected ByteBuffer makeCommandResponse(final byte[] responseData) {
        assert (responseData != null);
        HeaderOuterClass.Header header = makeHeader(0);
        byte[] headerData = header.toByteArray();
        return makeResponse(headerData, responseData);
    }

    protected ByteBuffer makeErrorResponse(final long requestID, final int errorCode, final String errorMessage) {
        HeaderOuterClass.Header header = makeHeader(errorCode);
        byte[] headerData = header.toByteArray();

        ResponseOk.response_ok.Builder errorResponseBuilder = ResponseOk.response_ok.newBuilder();

        errorResponseBuilder.setRequestId(requestID);
        errorResponseBuilder.setCode(errorCode);
        errorResponseBuilder.setMessage(errorMessage);

        ResponseOk.response_ok response = errorResponseBuilder.build();
        byte[] responseData = response.toByteArray();

        return makeResponse(headerData, responseData);
    }

    protected final boolean isError() {
        return error_;
    }

    protected final int getErrorCode() {
        return error_code_;
    }

    protected final String getErrorMessage() {
        return error_message_;
    }

    protected final void setError(boolean error) {
        error_ = error;
    }

    protected final void setErrorCode(final int errorCode) {
        error_code_ = errorCode;
    }

    protected final void setErrorMessage(final String errorMessage) {
        error_message_ = errorMessage;
    }

    @Override
    public void execute() {
        try {
            this.run();
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("failed to execute the task.msg={}, who={}",
                    exception.getMessage(), Arrays.toString(exception.getStackTrace()));
        }
    }

    /**
     * @return 任务是否完成
     */
    public abstract boolean run();

}