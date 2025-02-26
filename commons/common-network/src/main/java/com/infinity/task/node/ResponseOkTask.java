package com.infinity.task.node;

import com.google.protobuf.InvalidProtocolBufferException;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.network.RequestIDManager;
import com.infinity.protocol.ResponseOk;
import com.infinity.task.IResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ResponseOkTask extends AbstractBaseTask implements IResponse {
    private long request_id_ = 0L;

    @Override
    public void init() {
        super.init();
        parseResponse();
    }

    @Override
    public long getThreadMark() {
        return request_id_;
    }

    @Override
    public boolean run() {
        return true;
    }

    @Override
    public long getRequestID() {
        return request_id_;
    }

    private void parseResponse() {
        try {
            /*ByteBuffer responseRawData = getExtras();
            if(!responseRawData.hasRemaining())return;
            final int length = responseRawData.getInt();
            byte[] responseData = new byte[length];
            responseRawData.get(responseData);*/
            ResponseOk.response_ok responseOK = ResponseOk.response_ok.parseFrom(getExtras());
            request_id_ = responseOK.getRequestId();
            RequestIDManager.getInstance().addResponse(request_id_, this);
        } catch (InvalidProtocolBufferException e) {
            log.error("failed to parse the response data.");
            e.printStackTrace();
        }
    }
}
