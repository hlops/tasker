package com.hlops.tasker.impl;

import com.hlops.tasker.task.Task;

import java.util.concurrent.FutureTask;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/30/13
 * Time: 1:41 PM
 */
public class PriorityFutureTask<T> extends FutureTask<T> implements Comparable {

    private Task<T> task;

    public PriorityFutureTask(Task<T> task) {
        super(task);
        this.task = task;
    }

    Task<T> getTask() {
        return task;
    }

    public int compareTo(Object o) {
        return task.compareTo(((PriorityFutureTask) o).getTask());
    }
}
