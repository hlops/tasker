package com.hlops.tasker.task;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:52 PM
 */
public interface PrioritizedTask<T> extends Task<T> {

    int getPriority();

}
