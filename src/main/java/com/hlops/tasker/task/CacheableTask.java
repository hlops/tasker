package com.hlops.tasker.task;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:50 PM
 */
public interface CacheableTask<T> extends Task<T> {

    Object getId();

    long getAliveTime();
}
