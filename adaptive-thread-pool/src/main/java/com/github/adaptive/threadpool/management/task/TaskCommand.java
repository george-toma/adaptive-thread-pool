package com.github.adaptive.threadpool.management.task;

import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.task.Task;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class TaskCommand extends AbstractTaskManagement implements Command<Task> {

    public TaskCommand(TaskState taskState) {
        super(taskState);
    }

    @Override
    public List<Task> shutdown() {
        return taskState.drainTask();
    }

    @Override
    public void clear() {
        taskState.clear();
    }

    @Override
    public void add(Task... task) {

        if (task.length == 1) {
            taskState.put(task[0]);
        } else {
            for (int i = 0; i < task.length; i++)
                taskState.put(task[i]);
        }
    }

    @Override
    public Task remove() {
        return taskState.poll();
    }

    @Override
    public void remove(int numberOfTasks) {
        if (numberOfTasks < 0) {
            return;
        }
        for (int i = 0; i < numberOfTasks; i++) {
            remove();
        }
    }
}
