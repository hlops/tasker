package com.hlops.tasker;

import com.hlops.tasker.task.Task;

import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:36 PM
 */
public interface QueueService {

    <T> Future<T> executeTask(Task<T> task);
}
