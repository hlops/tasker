package com.hlops.tasker.impl;

import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 1/13/14
 * Time: 5:04 PM
 */
public class TaskAlreadyInCacheException extends RuntimeException {

    private Future cachedTask;

    public TaskAlreadyInCacheException(Future cachedTask) {
        this.cachedTask = cachedTask;
    }

    public Future getCachedTask() {
        return cachedTask;
    }
}
