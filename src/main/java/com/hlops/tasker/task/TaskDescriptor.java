package com.hlops.tasker.task;

import com.hlops.tasker.QueueService;

import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 4:08 PM
 */
public interface TaskDescriptor {

    //setPriority();

    //QueueService getQueueService();

    void waitFor(Task... task) throws InterruptedException;

    void waitFor(long millis, Task... task) throws InterruptedException;
}
