package com.hlops.tasker.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tasker.task.Task;
import com.hlops.tasker.task.impl.TaskImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:40 PM
 */
public class QueueServiceImplTest extends Assert {

    private static final int poolSize = 10;
    private QueueService queueService;

    @Before
    public void setUp() throws Exception {
        queueService = new QueueServiceImpl(poolSize);
    }

    @Test
    public void testTask() throws Exception {
        Future[] futures = new Future[100];
        for (int i = 0; i < futures.length; i++) {
            Task<String> task = new TaskImpl<String>() {
                public String call() throws Exception {
                    return "Hello!";
                }
            };
            futures[i] = queueService.executeTask(task);
        }

        for (int i = 0; i < futures.length; i++) {
            @SuppressWarnings({"unchecked"}) Future<String> future = futures[i];
            assertNotNull(future);
            assertEquals("Hello!", future.get());
        }
    }

}
