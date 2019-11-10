package com.github.adaptive.threadpool.mutator;


import com.github.adaptive.threadpool.metric.TaskMetrics;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class LoggerDistanceMutator extends AbstractThreadPoolMutator {

    @Override
    public void mutateThreadPoolSize(TaskMetrics... taskMetrics) {
        log.info("Logger mutator");
    }

    @Override
    public String getName() {
        return "logger";
    }
}
