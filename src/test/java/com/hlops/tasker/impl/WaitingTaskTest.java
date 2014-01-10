package com.hlops.tasker.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tasker.task.Task;
import com.hlops.tasker.task.impl.TaskImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 1/10/14
 * Time: 3:31 PM
 */
public class WaitingTaskTest extends Assert {

    private static final int poolSize = 11;
    private QueueService queueService;

    @Before
    public void setUp() throws Exception {
        queueService = new QueueServiceImpl(poolSize);
    }

    @Test
    public void testCacheableTask() throws Exception {
        final AtomicBoolean isOk = new AtomicBoolean(true);

        Future[] parentFutures = new Future[10];
        for (int i = 0; i < parentFutures.length; i++) {
            final int parentNumber = i;
            ParentTask task = new ParentTask("parent_" + parentNumber) {
                public String call() throws Exception {
/*
                    Future[] childFutures = new Future[10];
                    for (int i = 0; i < childFutures.length; i++) {
                        childFutures[i] = queueService.executeTask(new ChildTaskImpl("child_" + parentNumber + "_" + i, 10));
                    }
                    for (int i = 0; i < childFutures.length; i++) {
                        assertEquals("child_" + parentNumber + "_" + i, childFutures[i].get());
                    }
*/
                    return getName();
                }

            };
            parentFutures[i] = queueService.executeTask(task);
        }

        for (int i = 0; i < parentFutures.length; i++) {
            assertNotNull(parentFutures[i]);
            assertEquals("parent_" + i, parentFutures[i].get());
        }

        if (!isOk.get()) {
            fail();
        }
    }


    abstract class ParentTask extends TaskImpl<String> implements Task<String> {

        private String name;

        public ParentTask(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    class ChildTaskImpl extends TaskImpl<String> implements Task<String> {

        private String name;
        private long timeout;

        ChildTaskImpl(String name, long timeout) {
            this.name = name;
            this.timeout = timeout;
        }

        public String call() throws Exception {
            Thread.sleep(timeout);
            return name;
        }
    }
}
