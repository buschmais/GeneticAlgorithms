package com.buschmais.javaspektrum.geneticalgorithms.resourceplanning.multiobjective;

/**
 * Class representing a task to schedule
 *
 * @author Stephan Pirnbaum
 */
public class Task {

    /**
     * The workload (aka items to produce) of this task
     */
    private final long workload;

    /**
     * Creates a task with the specified workload
     *
     * @param workload The workload
     */
    Task(long workload) {
        this.workload = workload;
    }

    /**
     * Returns the workload of this task
     *
     * @return The workload
     */
    long getWorkload() {
        return workload;
    }

    @Override
    public String toString() {
        return "Task[Workload: " + workload + "]";
    }
}
