package com.github.adaptive.threadpool.flow;

import com.github.adaptive.threadpool.listener.event.Event;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log4j2
public abstract class EventPublisher extends SubmissionPublisher<Event> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public EventPublisher( ExecutorService eventsThreadPool) {
        super(eventsThreadPool, EventFlowConstant.EVENT_PUBLISHER_QUEUE_SIZE);
    }

    /**
     * Publishes the given item to each current subscriber by
     * asynchronously invoking its {@link java.util.concurrent.Flow.Subscriber#onNext(Object)
     * onNext} method, blocking uninterruptibly while resources for any
     * subscriber are unavailable. This method returns an estimate of
     * the maximum lag (number of items submitted but not yet consumed)
     * among all current subscribers. This value is at least one
     * (accounting for this submitted item) if there are any
     * subscribers, else zero.
     * <p>
     * <p>If the Executor for this publisher throws a
     * RejectedExecutionException (or any other RuntimeException or
     * Error) when attempting to asynchronously notify subscribers,
     * then this exception is rethrown, in which case not all
     * subscribers will have been issued this item.
     *
     * @param item the (non-null) item to publish
     * @return the estimated maximum lag among subscribers
     * @throws IllegalStateException                           if closed
     * @throws NullPointerException                            if item is null
     * @throws java.util.concurrent.RejectedExecutionException if thrown by Executor
     */
    @Override
    public int submit(Event item) {
        if (!isClosed()) {
            readWriteLock.readLock().lock();
            try {
                if (!isClosed()) {
                    log.debug("Event [{}] was published", item);
                    return super.submit(item);
                }
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
        else{
            log.info("Event publisher is closed for event [{}]", item);
        }
        return 0;
    }

    public void shutdown() {
        readWriteLock.writeLock().lock();
        try {
            close();
            ((ExecutorService) getExecutor()).shutdown();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
