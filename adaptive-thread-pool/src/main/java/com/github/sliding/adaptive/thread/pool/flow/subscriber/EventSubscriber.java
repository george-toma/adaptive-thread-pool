package com.github.sliding.adaptive.thread.pool.flow.subscriber;

import com.github.sliding.adaptive.thread.pool.flow.EventFlowConstant;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.mutator.ThreadPoolMutator;
import com.github.sliding.adaptive.thread.pool.report.SharedMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import com.github.sliding.adaptive.thread.pool.report.repository.TaskMetricsRepository;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.concurrent.Flow;

@Log4j2
public abstract class EventSubscriber implements Flow.Subscriber<Event> {

    protected Flow.Subscription subscription;
    protected final String threadPoolIdentifier;
    protected final TaskMetricsRepository taskMetricsRepository;
    protected final ThreadPoolMutator threadPoolMutator;

    public EventSubscriber(String threadPoolIdentifier, ThreadPoolMutator threadPoolMutator) {
        this.threadPoolIdentifier = threadPoolIdentifier;
        this.taskMetricsRepository = SharedMetricsRepository.load(threadPoolIdentifier).get();
        this.threadPoolMutator = threadPoolMutator;
    }

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

    protected final Optional<TaskMetrics.Builder> loadTaskMetrics(Event event) {
        return taskMetricsRepository
                .loadTaskBuilder(event.getIdentifier());


    }

    protected final void removeTaskMetric(Event event, String identifier) {
        taskMetricsRepository.removeTaskMetric(identifier);
        log.info("Event [{}] removed task metric [identifier: {}] from pipeline processing", event, identifier);

    }

    public abstract EventType eventType();
}