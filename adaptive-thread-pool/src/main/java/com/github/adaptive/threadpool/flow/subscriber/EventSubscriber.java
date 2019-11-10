package com.github.adaptive.threadpool.flow.subscriber;

import com.github.adaptive.threadpool.listener.event.Event;
import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.flow.EventFlowConstant;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Flow;

@Log4j2
public abstract class EventSubscriber implements Flow.Subscriber<Event> {

    protected Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND); //a value of  Long.MAX_VALUE may be considered as effectively unbounded
    }

    @Override
    public void onNext(Event event) {
        log.trace("Subscriber onNext event [{}]", event);
        //request new messages
        subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND);
    }

    @Override
    public void onError(Throwable t) {
        log.warn("An error occured while processing the event", t);
    }

    @Override
    public void onComplete() {
        log.debug("Event completed");
    }

    public abstract EventType eventType();
}