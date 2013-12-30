package com.hlops.tasker.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tasker.task.Task;
import com.hlops.tasker.task.impl.PrioritizedTaskImpl;
import com.hlops.tasker.task.impl.TaskImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    public void testPriorityTask1() throws Exception {
        final int size = 100;
        final AtomicInteger counter = new AtomicInteger();
        Future[] futures = new Future[size];

        final AtomicBoolean isOk = new AtomicBoolean(true);
        for (int i = 0; i < futures.length; i++) {
            final int finalI = i;
            Task<String> task = new MyPrioritizedTaskImpl<String>(i, finalI) {
                public String call() throws Exception {
                    Thread.sleep(size);
                    return "ok";
                }

                @Override
                public void afterExecute(Throwable t) {
                    final int n = counter.intValue();
                    final int r1 = n / poolSize;
                    final int r2 = (size - this.getPriority()) / poolSize;
                    if (r1 > 0) {
                        try {
                            assertTrue(r1 >= r2);
                        } catch (AssertionError e) {
                            isOk.set(false);
                            throw e;
                        }
                    }
                    counter.incrementAndGet();
                }
            };
            futures[i] = queueService.executeTask(task);
        }

        for (int i = 0; i < futures.length; i++) {
            @SuppressWarnings({"unchecked"}) Future<String> future = futures[i];
            assertNotNull(future);
            assertEquals("ok", future.get());
        }

        if (!isOk.get()) {
            fail();
        }
    }

    @Test
    public void testPriorityTask2() throws Exception {
        final int size = 100;
        final AtomicInteger counter = new AtomicInteger();
        Future[] futures = new Future[size];

        final AtomicBoolean isOk = new AtomicBoolean(true);
        for (int i = 0; i < futures.length; i++) {
            Task<String> task = new MyPrioritizedTaskImpl<String>(i, 0) {
                public String call() throws Exception {
                    Thread.sleep(10);
                    return "ok";
                }

                @Override
                public void afterExecute(Throwable t) {
                    final int n = counter.intValue();
                    final int r1 = n / poolSize;
                    final int r2 = this.getId() / poolSize;
                    if (r1 > 0) {
                        try {
                            assertTrue(getId() + " " + r1 + " " + r2, r1 == r2);
                        } catch (AssertionError e) {
                            isOk.set(false);
                            throw e;
                        }
                    }
                    counter.incrementAndGet();
                }
            };
            futures[i] = queueService.executeTask(task);
        }

        for (int i = 0; i < futures.length; i++) {
            @SuppressWarnings({"unchecked"}) Future<String> future = futures[i];
            assertNotNull(future);
            assertEquals("ok", future.get());
        }

        if (!isOk.get()) {
            fail();
        }
    }

    abstract class MyPrioritizedTaskImpl<T> extends PrioritizedTaskImpl<T> {

        private int id;

        protected MyPrioritizedTaskImpl(int id, int priority) {
            super(priority);
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
