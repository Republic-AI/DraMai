package com.infinity.common.utils;

import java.util.concurrent.locks.Lock;

public final class LockGuard<T extends Lock> implements AutoCloseable
{
    private final T locker_;
    public LockGuard(final T locker)
    {
        locker_ = locker;
        assert locker_ != null;
        locker_.lock();
    }

    @Override
    public void close()
    {
        try
        {
            assert locker_ != null;
            locker_.unlock();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
