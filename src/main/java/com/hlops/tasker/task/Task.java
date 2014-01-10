package com.hlops.tasker.task;

import com.hlops.tasker.impl.QueueServiceImpl;

import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:34 PM
 */
public interface Task<T> extends Callable<T>, Comparable<Task> {

    void beforeExecute(Thread t);

    void afterExecute(Throwable t);
}