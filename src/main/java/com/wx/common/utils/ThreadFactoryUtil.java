package com.wx.common.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadFactoryUtil implements ThreadFactory {

    private final AtomicLong threadIndex = new AtomicLong(0);

    private final String threadNamePrefix;

    public ThreadFactoryUtil(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadNamePrefix + this.threadIndex.incrementAndGet());
    }
}
