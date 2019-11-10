package com.github.adaptive.threadpool.flow.processor;

import com.github.adaptive.threadpool.listener.event.Event;
import com.github.adaptive.threadpool.flow.EventFlowConstant;
import com.github.adaptive.threadpool.flow.EventPublisher;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.function.Predicate;

@Log4j2
public class EventFilterProcessor
        extends EventPublisher implements Flow.Processor<Event, Event> {


    private final Predicate<Event> predicate;
    private Flow.Subscription subscription;


    public EventFilterProcessor(Predicate<Event> predicate) {
        super(Executors.newSingleThreadExecutor());
        this.predicate = predicate;
    }

    /**
     * Method invoked prior to invoking any other Subscriber
     * methods for the given Subscription. If this method throws
     * an exception, resulting behavior is not guaranteed, but may
     * cause the Subscription not to be established or to be cancelled.
     * <p>
     * <p>Typically, implementations of this method invoke {@code
     * subscription.request} to enable receiving items.
     *
     * @param subscription a new subscription
     */
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND);
    }

    /**
     * Method invoked with a Subscription's next item.  If this
     * method throws an exception, resulting behavior is not
     * guaranteed, but may cause the Subscription to be cancelled.
     *
     * @param item the event to process
     */
    @Override
    public void onNext(Event item) {
        log.debug("Processor received event [{}]", item);
        if (predicate.test(item)) {
            submit(item);
        }
        subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND);
    }

    /**
     * Method invoked upon an unrecoverable error encountered by a
     * Publisher or Subscription, after which no other Subscriber
     * methods are invoked by the Subscription.  If this method
     * itself throws an exception, resulting behavior is
     * undefined.
     *
     * @param throwable the exception
     */
    @Override
    public void onError(Throwable throwable) {
        log.warn("An error occured while executing the processor", throwable);
    }

    /**
     * Method invoked when it is known that no additional
     * Subscriber method invocations will occur for a Subscription
     * that is not already terminated by error, after which no
     * other Subscriber methods are invoked by the Subscription.
     * If this method throws an exception, resulting behavior is
     * undefined.
     */
    @Override
    public void onComplete() {
        log.warn("Processor was executed with success");

    }

}
