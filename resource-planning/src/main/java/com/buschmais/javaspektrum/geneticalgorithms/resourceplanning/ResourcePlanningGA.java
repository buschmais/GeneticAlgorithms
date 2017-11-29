package com.buschmais.javaspektrum.geneticalgorithms.resourceplanning;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;

import java.util.*;

/**
 * Implementation of the resource scheduling example using Jenetics.
 *
 * @author Stephan Pirnbaum
 */
public class ResourcePlanningGA {

    /**
     * All available resources
     */
    private Resource[] resources;

    /**
     * All tasks to schedule
     */
    private Task[] tasks;

    /**
     * Creates a new instance and initializes the resources and tasks
     */
    private ResourcePlanningGA() {
        initializeResources();
        initializeTasks();
    }

    public static void main(String[] args) {
        ResourcePlanningGA ga = new ResourcePlanningGA();
        ga.computeSchedule();
    }

    /**
     * Computes a schedule with regard to the fitness and pretty prints it to the command line
     */
    private void computeSchedule() {
        /*
         * create a genotype that maps resources to tasks
         */
        Genotype<IntegerGene> genotype = Genotype.of(
                IntegerChromosome.of(0, resources.length - 1, tasks.length)
        );
        /*
         *create the GA engine with the specified parameters
         */
        Engine<IntegerGene, Double> engine = Engine
                .builder(this::fitness, genotype)
                .populationSize(500)
                .alterers(new SinglePointCrossover<>(1), new Mutator<>(0.01))
                .selector(new RouletteWheelSelector<>())
                .build();
        /*
         * Execute the genetic algorithm until there is no better solution in the last 500 generations
         */
        EvolutionResult<IntegerGene, Double> result = engine
                .stream()
                .limit(Limits.bySteadyFitness(500))
                .peek(g -> System.out.println("Generation: " + g.getGeneration() + " Best Fitness: " + g.getBestFitness()))
                .collect(EvolutionResult.toBestEvolutionResult());
        /*
         * interpret the data for pretty printing
         */
        Map<Resource, Set<Task>> schedule = new HashMap<>();
        Chromosome<IntegerGene> chromosome = result.getBestPhenotype().getGenotype().getChromosome();
        for (int i = 0; i < chromosome.length(); i++) {
            Resource resource = this.resources[chromosome.getGene(i).getAllele()];
            Task task = this.tasks[i];
            if (!schedule.containsKey(resource)) {
                schedule.put(resource, new HashSet<>());
            }
            schedule.get(resource).add(task);
        }
        /*
         * print the results
         */
        System.out.println("Finished");
        System.out.println("-----------------");
        System.out.println("Generation: " + result.getGeneration());
        System.out.println("Fitness: " + result.getBestFitness());
        System.out.println("Cost: " + computeCosts(result.getBestPhenotype().getGenotype().getChromosome()));
        System.out.println("Time: " + computeTime(result.getBestPhenotype().getGenotype().getChromosome()) + " Minutes");
        schedule.forEach((k, v) -> {
            System.out.println("Details: " + k + " " + v);
            System.out.println("  Time: " + v.stream().mapToDouble(t -> t.getWorkload() / k.getItemsPerMinute()).sum());
            System.out.println("  Cost: " + v.stream().mapToDouble(t -> (t.getWorkload() / k.getItemsPerMinute()) * k.getCostsPerMinute()).sum());
        });
    }

    /**
     * Computes the fitness of an individual by subtracting needed time and costs from 0
     *
     * @param individual The individual for which to calculate the fitness
     *
     * @return The individuals fitness
     */
    private Double fitness(final Genotype<IntegerGene> individual) {
        Chromosome<IntegerGene> chromosome = individual.getChromosome();
        return - computeTime(chromosome) - computeCosts(chromosome);
    }

    /**
     * Compute the time needed to execute the schedule
     *
     * @param chromosome The chromosome to compute the needed time for
     *
     * @return The needed time
     */
    private Double computeTime(Chromosome<IntegerGene> chromosome) {
        double accumulatedTime = 0d;
        for (int i = 0; i < chromosome.length(); i++) {
            Resource resource = this.resources[chromosome.getGene(i).getAllele()];
            double time = this.tasks[i].getWorkload() / resource.getItemsPerMinute();
            accumulatedTime += time;
        }
        return accumulatedTime;
    }

    /**
     * Compute the costs produced by executing the schedule
     *
     * @param chromosome The chromosome to compute the produced costs for
     *
     * @return The costs
     */
    private Double computeCosts(Chromosome<IntegerGene> chromosome) {
        double accumulatedCosts = 0d;
        for (int i = 0; i < chromosome.length(); i++) {
            Resource resource = this.resources[chromosome.getGene(i).getAllele()];
            double time = this.tasks[i].getWorkload() / resource.getItemsPerMinute();
            accumulatedCosts += time * resource.getCostsPerMinute();
        }
        return accumulatedCosts;
    }

    /**
     * Initializes the resources
     */
    private void initializeResources() {
        // create 20 resources
        final long[] itemsPerMinuteConfig = {10, 10, 10, 10, 10, 10, 25, 25, 25, 25, 30, 30, 30, 50, 50, 50, 50, 100, 100, 250};
        this.resources = new Resource[20];
        for (int i = 0; i < 20; i++) {
            this.resources[i] = new Resource(Math.pow(itemsPerMinuteConfig[i], 1.1), itemsPerMinuteConfig[i]);
        }
    }

    /**
     * Initializes the tasks
     */
    private void initializeTasks() {
        // create 100 tasks
        this.tasks = new Task[100];
        for (int i = 0; i < 100; i++) {
            long workload;
            switch (i % 8) {
                case 0:
                case 1:
                case 2:
                case 3:
                    workload = 250;
                    break;
                case 4:
                case 5:
                    workload = 1000;
                    break;
                case 6:
                case 7:
                    workload = 2500;
                    break;
                default:
                    workload = 7500;
                    break;
            }
            this.tasks[i] = new Task(workload);
        }
    }
}
