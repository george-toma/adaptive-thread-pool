package com.github.sliding.adaptive.thread.pool.flow;

import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;

@Log4j2
public abstract class EventPublisher extends SubmissionPublisher<Event> {
    protected final String sharedEventPublisherName;

    public EventPublisher(Executor executor, int maxBufferCapacity, String sharedEventPublisherName) {
        super(executor, maxBufferCapacity);
        this.sharedEventPublisherName = sharedEventPublisherName;
        SharedEventPublisher.store(this, sharedEventPublisherName);
    }

    public EventPublisher() {
        super(Executors.newSingleThreadExecutor(), EventFlowConstant.EVENT_PUBLISHER_QUEUE_SIZE);
        this.sharedEventPublisherName = SharedEventPublisher.DEFAULT_EVENT_PUBLISHER;
        SharedEventPublisher.store(this, sharedEventPublisherName);
    }

    public EventPublisher(Executor executor, int maxBufferCapacity) {
        super(executor, maxBufferCapacity);
        this.sharedEventPublisherName = SharedEventPublisher.DEFAULT_EVENT_PUBLISHER;
        SharedEventPublisher.store(this, sharedEventPublisherName);
    }

    /**
     * Publishes the given item to each current subscriber by
     * asynchronously invoking its {@link Flow.Subscriber#onNext(Object)
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
        log.debug("Event [{}] was published", item);
        return super.submit(item);
    }

    public void close() {
        super.close();
        SharedEventPublisher.remove(sharedEventPublisherName);
    }
}
