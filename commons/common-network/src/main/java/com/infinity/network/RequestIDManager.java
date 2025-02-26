package com.infinity.network;


import com.infinity.common.Pair;
import com.infinity.task.IRequest;
import com.infinity.task.IResponse;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class RequestIDManager
{
    private static class RequestIDManagerHolder
    {
        private static RequestIDManager kInstance = new RequestIDManager();
    }

    public static RequestIDManager getInstance()
    {
        return RequestIDManagerHolder.kInstance;
    }

    private int high32_ = 0;
    private Object request_id_locker_ = new Object();
    private final long kUint32Max = 4294967295L;

    private final Map<Long, IRequest> request_map_ = new ConcurrentHashMap<>();
    private final Map<Long, IResponse> response_map_ = new ConcurrentHashMap<>();

    private Object client_request_locker_ = new Object();
    private final Set<Pair<Long, Long>> client_request_ = new ConcurrentSkipListSet<>();

    public void dispose()
    {
        request_map_.clear();
        response_map_.clear();
        client_request_.clear();
    }

    public final void addResponse(final long requestID, final IResponse response)
    {
        response_map_.put(requestID, response);
    }

    public final IResponse getResponse(final long requestID)
    {
        return response_map_.get(requestID);
    }

    public final IResponse removeResponse(final long requestID)
    {
        return response_map_.remove(requestID);
    }

    public final boolean hasResponse(final long requestID)
    {
        return response_map_.containsKey(requestID);
    }

    public final void addRequest(final long requestID, final IRequest request)
    {
        request_map_.put(requestID, request);
    }

    public final IRequest getRequest(final long requestID)
    {
        return request_map_.get(requestID);
    }

    public final IRequest removeRequest(final long requestID)
    {
        return request_map_.remove(requestID);
    }

    public final boolean hasRequest(final long requestID)
    {
        return request_map_.containsKey(requestID);
    }

    public boolean addRequestWithPlayerID(final long playerID, final long requestID)
    {
        synchronized (client_request_locker_)
        {
            return client_request_.add(new Pair<Long, Long>(playerID, requestID));
        }
    }

    public boolean hasRequestWithPlayerID(final long playerID, final long requestID)
    {
        synchronized(client_request_locker_)
        {
            return client_request_.contains(new Pair<Long, Long>(playerID, requestID));
        }
    }

    public void removeRequestWithPlayerID(final long playerID, final long requestID)
    {
        synchronized (client_request_locker_)
        {
            client_request_.remove(new Pair<Long, Long>(playerID, requestID));
        }
    }

    public final long RequestID(boolean toMultiDir) // Send to compute node via gateway and directly
    {
        long low32 = System.currentTimeMillis() / 1000;
        if(low32 > kUint32Max)
            low32 -= kUint32Max;

        synchronized (request_id_locker_)
        {
            high32_++;
            if(high32_ < 0) high32_ = 0; /* make sure high-32 is a positive value
            ( for the negative is reserved for client command to multiple directions) */
            long letag = high32_;
            letag <<= 32;
            letag += low32;
            if(toMultiDir)
                return -letag;
            return letag;
        }
    }
}