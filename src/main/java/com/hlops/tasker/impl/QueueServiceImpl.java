package com.hlops.tasker.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.Task;
import com.hlops.tasker.task.TaskDescriptor;

import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:40 PM
 */
public class QueueServiceImpl implements QueueService, TaskDescriptor {

    private final ThreadPoolExecutor threadExecutor;
    private final ConcurrentHashMap<Object, Future> cache = new ConcurrentHashMap<Object, Future>();

    public QueueServiceImpl(int poolSize) {
        threadExecutor = new ThreadPoolExecutor(poolSize, poolSize, 60L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>()) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                final Task task = ((PriorityFutureTask) r).getTask();
                task.setTaskDescriptor(QueueServiceImpl.this);
                task.beforeExecute(t);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                final Task task = ((PriorityFutureTask) r).getTask();
                task.afterExecute(t);
            }

            @Override
            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                return new PriorityFutureTask<T>((Task<T>) callable);
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

    public void waitFor(long millis, Task... task) throws InterruptedException {
    }

    private <T> Object getId(Task<T> task) {
        if (task instanceof CacheableTask<?>) {
            return ((CacheableTask) task).getId();
        }
        return null;
    }
}
