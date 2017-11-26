package com.buschmais.javaspektrum.geneticalgorithms.infinitemonkeys.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Implementation of the infinite monkey theorem using a basic genetic algorithm. The code aims to create the string
 * "to be or not to be".
 *
 * @author Stephan Pirnbaum
 */
public class BasicGeneticAlgorithmExample {

    /**
     * The target string
     */
    private static final String target = "to be or not to be";

    /**
     * The allels
     */
    private static final List<Character> allels = new ArrayList<>(27);

    static {
        allels.add((char) 32);
        IntStream.rangeClosed(97, 122).forEach(i -> allels.add((char) i));
    }

    public static void main(String[] args) {
        // set population size
        Population.populationSize = 100;
        // create initial population
        Population population = Population.createRandonInitialPopulation();
        // evolve until solution is found
        while (!(population.bestIndividual.fitness == 1d)) {
            population = population.evolveToNextGeneration();
        }
        System.out.println("Finished");
    }

    /**
     * Class representing the population at one generation with all its individuals
     */
    private static class Population {

        /**
         * The size of the population
         */
        private static int populationSize;

        /**
         * The generation of this population
         */
        private int generation;

        /**
         * The individual in this population
         */
        private List<Chromosome> individuals;

        /**
         * The best individual of this generation
         */
        private Chromosome bestIndividual;

        private Population(int generation, List<Chromosome> individuals) {
            this.generation = generation;
            this.individuals = individuals;
            this.bestIndividual = this.individuals.stream().sorted(Comparator.comparingDouble(c -> -c.getFitness())).findFirst().orElse(null);
            System.out.println("Generation: " + generation + " Fitness: " + this.bestIndividual.getFitness() + " Best: " + this.bestIndividual.getPhenotype());
        }

        /**
         * Creates a random population at generation 0
         *
         * @return A newly create population
         */
        static Population createRandonInitialPopulation() {
            List<Chromosome> individuals = new ArrayList<>(populationSize);
            for (int i = 0; i < populationSize; i++) {
                individuals.add(Chromosome.createRandomChromosome());
            }
            return new Population(0, individuals);
        }

        /**
         * Applies the steps selection, crossover, and mutation to the current population and returns the result as a new
         * population with the generation number incremented by 1
         *
         * @return The next generation
         */
        Population evolveToNextGeneration() {
            /*
             * Prepare the fitness proportional selection by creating a mating pool
             */
            List<Chromosome> matingPool = new ArrayList<>();
            for (Chromosome c : this.individuals) {
                for (int i = 0; i < c.fitness * target.length(); i++) {
                    matingPool.add(c);
                }
            }
            /*
             * Create the next generation
             */
            List<Chromosome> nextGeneration = new ArrayList<>(populationSize);
            for (int i = 0; i < populationSize; i++) {
                /*
                 * Choose two individuals from the mating pool and let them create an offspring individual
                 */
                Chromosome a = matingPool.get(ThreadLocalRandom.current().nextInt(matingPool.size()));
                Chromosome b = matingPool.get(ThreadLocalRandom.current().nextInt(matingPool.size()));
                a = a.crossover(b);
                /*
                 * Apply mutation to the offspring
                 */
                nextGeneration.add(a.mutate(0.01));
            }
            return new Population(++this.generation, nextGeneration);
        }
    }

    /**
     * A class representing storing the genetic material of one individual
     */
    private static class Chromosome {

        /**
         * The genetic material
         */
        private final char[] genes;

        /**
         * The fitness of this individual
         */
        private final double fitness;

        /**
         * Creates a new chromosome with the given genetic material
         *
         * @param genes The char sequence
         */
        private Chromosome(char[] genes) {
            this.genes = genes;
            this.fitness = computeFitness();
        }

        /**
         * Creates a random chromosome from the set of available allels ({@link BasicGeneticAlgorithmExample#allels})
         *
         * @return The newly created chromosome
         */
        static Chromosome createRandomChromosome() {
            char[] newGenes = new char[target.length()];
            for (int i = 0; i < newGenes.length; i++) {
                newGenes[i] = allels.get(ThreadLocalRandom.current().nextInt(0, allels.size()));
            }
            return new Chromosome(newGenes);
        }

        /**
         * Creates a chid individual by combining the genetic material of this and the other chromosome. This method uses
         * sinle point cross over with the middle index s crossover point.
         *
         * @param other The other chromosome
         *
         * @return The offspring
         */
        Chromosome crossover(Chromosome other) {
            char[] newGenes = new char[this.genes.length];
            System.arraycopy(this.genes, 0, newGenes, 0, this.genes.length / 2);
            System.arraycopy(other.genes, this.genes.length / 2, newGenes, this.genes.length / 2, this.genes.length / 2);
            return new Chromosome(newGenes);
        }

        /**
         * Mutates the genetic material of this chromosome by changing each gene with the specified probability
         *
         * @param mutationRate The probability to vary a gene
         *
         * @return A newly created gene
         */
        Chromosome mutate(double mutationRate) {
            char[] newGenes = new char[this.genes.length];
            for (int i = 0; i < this.genes.length; i++) {
                if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
                    newGenes[i] = allels.get(ThreadLocalRandom.current().nextInt(0, allels.size()));
                } else {
                    newGenes[i] = this.genes[i];
                }
            }
            return new Chromosome(newGenes);
        }

        /**
         * Computes the fitness of this individual by comparing its genetic material to the sought-for solution.
         *
         * @return A number between 0 and 1 representing the degree of conformance to the actual solution.
         */
        private double computeFitness() {
            int fitness = 0;
            for (int i = 0; i < genes.length; i++) {
                if (this.genes[i] == target.charAt(i)) {
                    fitness++;
                }
            }
            return (double) fitness / this.genes.length;
        }

        /**
         * Returns the fitness of this individual
         *
         * @return The fitness of this individual
         */
        double getFitness() {
            return this.fitness;
        }

        /**
         * Returns the phenotype representation of this individual
         *
         * @return The phenotype
         */
        String getPhenotype() {
            return new String(this.genes);
        }
    }
}
