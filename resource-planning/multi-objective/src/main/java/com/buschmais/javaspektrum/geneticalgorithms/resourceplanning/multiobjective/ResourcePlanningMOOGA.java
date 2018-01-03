package com.buschmais.javaspektrum.geneticalgorithms.resourceplanning.multiobjective;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Limits;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.NSGA2Selector;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;

import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;

public class ResourcePlanningMOOGA {

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
    private ResourcePlanningMOOGA() {
        initializeResources();
        initializeTasks();
    }

    public static void main(String[] args) {
        ResourcePlanningMOOGA ga = new ResourcePlanningMOOGA();
        ga.computeSchedule();
    }

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
        Engine<IntegerGene, Vec<double[]>> engine = Engine
                .builder(
                        i -> Vec.of(computeTime(i), computeCosts(i)),
                        genotype
                )
                .executor(new ForkJoinPool())
                .populationSize(500)
                .survivorsSelector(NSGA2Selector.vec())
                .alterers(new SinglePointCrossover<>(1), new Mutator<>(0.01))
                .minimizing()
                .build();
        /*
         * Execute the genetic algorithm for 2000 generations
         */
        ISeq<Phenotype<IntegerGene, Vec<double[]>>> paretoFrontier = engine.stream()
                .limit(Limits.byFixedGeneration(2000))
                .peek(i -> System.out.print("\rGeneration: " + i.getGeneration()))
                .collect(MOEA.toParetoSet());
        System.out.println("\n\n\n============\nPareto Frontier\n============\n");
        paretoFrontier
                .stream()
                .sorted(Comparator.comparingDouble(p -> p.getFitness().data()[0]))
                .forEach(p -> System.out.println("Time: " + p.getFitness().data()[0] + " Costs: " + p.getFitness().data()[1]));
    }

    /**
     * Compute the time needed to execute the schedule
     *
     * @param genotype The genotype to compute the needed time for
     *
     * @return The needed time
     */
    private Double computeTime(Genotype<IntegerGene> genotype) {
        Chromosome<IntegerGene> chromosome = genotype.getChromosome();
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
     * @param genotype The genotype to compute the produced costs for
     *
     * @return The costs
     */
    private Double computeCosts(Genotype<IntegerGene> genotype) {
        Chromosome<IntegerGene> chromosome = genotype.getChromosome();
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
                default:
                    workload = 2500;
                    break;
            }
            this.tasks[i] = new Task(workload);
        }
    }
}
