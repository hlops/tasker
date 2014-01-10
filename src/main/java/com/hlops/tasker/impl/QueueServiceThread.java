package com.hlops.tasker.impl;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 1/10/14
 * Time: 5:46 PM
 */
class QueueServiceThread extends Thread {

    ThreadLocal<AtomicBoolean> isPoolSizeInflated = new ThreadLocal<AtomicBoolean>() {
        @Override
        protected AtomicBoolean initialValue() {
            return new AtomicBoolean(false);
        }
    };

    public QueueServiceThread(ThreadGroup group, Runnable r, String s, int i) {
    }

    public Boolean getPoolSizeInflated() {
        return isPoolSizeInflated.get().get();
    }

    public boolean setPoolSizeInflated(boolean value) {
        return isPoolSizeInflated.get().compareAndSet(!value, value);
    }
}
