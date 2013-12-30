package com.hlops.tasker.task.impl;

import com.hlops.tasker.task.Task;
import com.hlops.tasker.task.TaskDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/30/13
 * Time: 1:21 PM
 */
public abstract class TaskImpl<T> implements Task<T> {

    private final long time = System.nanoTime();
    private TaskDescriptor taskDescriptor;

    protected TaskDescriptor getTaskDescriptor() {
        return taskDescriptor;
    }

    public void setTaskDescriptor(TaskDescriptor taskDescriptor) {
        this.taskDescriptor = taskDescriptor;
    }

    public void beforeExecute(Thread t) {
    }

    public void afterExecute(Throwable t) {
    }

    public int compareTo(Task task) {
        if (task != this && task instanceof TaskImpl) {
            return Long.compare(time, ((TaskImpl) task).time);
        }
        return 0;
    }

}
