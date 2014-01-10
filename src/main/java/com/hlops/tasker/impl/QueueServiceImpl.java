package com.hlops.tasker.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.Task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:40 PM
 */
public class QueueServiceImpl implements QueueService {

    private final ThreadPoolExecutor threadExecutor;
    private final ConcurrentHashMap<Object, Future> cache = new ConcurrentHashMap<Object, Future>();
    private final AtomicInteger poolSize = new AtomicInteger();

    public QueueServiceImpl(int poolSize) {

        this.poolSize.set(poolSize);

        threadExecutor = new ThreadPoolExecutor(poolSize, poolSize, 60L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(), new QueueServiceThreadFactory()) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                final Task task = ((PriorityFutureTask) r).getTask();
                task.beforeExecute(t);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                final Task task = ((PriorityFutureTask) r).getTask();
                task.afterExecute(t);
                checkPoolSize(false);
            }

            @Override
            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                System.out.println(callable);
                checkPoolSize(true);
                final PriorityFutureTask<T> task = new PriorityFutureTask<T>((Task<T>) callable);
                return task;
            }
        };
    }

    public <T> Future<T> executeTask(Task<T> task) {
        Object id = getId(task);
        if (id != null) {
            //noinspection unchecked
            Future<T> taskFuture = cache.get(id);
            if (taskFuture != null) {
                return taskFuture;
            }
            synchronized (cache) {
                //noinspection unchecked
                taskFuture = cache.get(id);
                if (taskFuture != null) {
                    return taskFuture;
                }
                Future<T> result = threadExecutor.submit(task);
                cache.put(id, result);
                return result;
            }
        }
        return threadExecutor.submit(task);
    }

    public void waitFor(Task... task) throws InterruptedException {
        this.waitFor(0, task);
    }

    public void waitFor(long millis, Task... tasks) throws InterruptedException {
        long t = System.currentTimeMillis();
        for (Task task : tasks) {
            long interval = millis - System.currentTimeMillis() + t;
            if (interval < 0) {
                // todo: arguments
                throw new InterruptedException();
            }
            //task.join(interval);

        }
    }

    private void checkPoolSize(boolean isIncrement) {
        if (Thread.currentThread() instanceof QueueServiceThread) {
            if (isIncrement) {
                if (((QueueServiceThread) Thread.currentThread()).setPoolSizeInflated(true)) {
                    updatePoolSize(poolSize.incrementAndGet());
                }
            } else {
                if (((QueueServiceThread) Thread.currentThread()).setPoolSizeInflated(false)) {
                    updatePoolSize(poolSize.decrementAndGet());
                }
            }
        }
    }

    private void updatePoolSize(int n) {
        System.out.println(n);
        threadExecutor.setCorePoolSize(n);
    }

    private <T> Object getId(Task<T> task) {
        if (task instanceof CacheableTask<?>) {
            return ((CacheableTask) task).getId();
        }
        return null;
    }
}
