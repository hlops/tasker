package com.hlops.tasker.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tasker.task.CacheableTask;
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
public class CacheableTaskTest extends Assert {

    private static final int poolSize = 10;
    private QueueService queueService;

    @Before
    public void setUp() throws Exception {
        queueService = new QueueServiceImpl(poolSize);
    }

    @Test
    public void testCacheableTask() throws Exception {
        Future[] futures = new Future[100];
        for (int i = 0; i < futures.length; i++) {
            Task<Long> task = new MyCacheableTaskImpl<Long>() {
                public Long call() throws Exception {
                    return System.nanoTime();
                }
            };
            futures[i] = queueService.executeTask(task);
        }

        long time = 0;
        for (Future future : futures) {
            assertNotNull(future);
            if (time == 0) {
                time = (Long) future.get();
            } else {
                assertEquals(time, future.get());
            }
        }
    }

    abstract class MyCacheableTaskImpl<T> extends TaskImpl<T> implements CacheableTask<T> {

        public Object getId() {
            return "uniqueId";
        }

        public long getAliveTime() {
            return 1000;
        }

    }
}
