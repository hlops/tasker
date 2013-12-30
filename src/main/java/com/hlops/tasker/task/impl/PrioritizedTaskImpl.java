package com.hlops.tasker.task.impl;

import com.hlops.tasker.task.Task;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/30/13
 * Time: 2:58 PM
 */
public abstract class PrioritizedTaskImpl<T> extends TaskImpl<T> {

    private int priority;

    protected PrioritizedTaskImpl(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public int compareTo(Task task) {
        if (task instanceof PrioritizedTaskImpl) {
            int compare = Integer.compare(((PrioritizedTaskImpl) task).getPriority(), getPriority());
            if (compare != 0) {
                return compare;
            }
        }
        return super.compareTo(task);
    }
}
