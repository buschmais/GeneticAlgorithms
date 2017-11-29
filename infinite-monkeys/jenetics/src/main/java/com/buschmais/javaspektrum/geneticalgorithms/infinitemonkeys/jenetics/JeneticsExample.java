package com.buschmais.javaspektrum.geneticalgorithms.infinitemonkeys.jenetics;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Limits;
import io.jenetics.util.CharSeq;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Infinite monkey theorem implementation utilizing the Jenetics framework
 *
 * @author Stephan Pirnbaum
 */
public class JeneticsExample {

    private static final String target = "to be or not to be";

    public static void main(String[] args) {
        final StringBuilder allels = new StringBuilder(" ");
        IntStream.rangeClosed(97, 122).forEach(i -> allels.append((char) i));

        /*
         * Specify the genetic structure: number of chromosomes, allowed allels, and length
         */
        Genotype<CharacterGene> genotype = Genotype.of(
                new CharacterChromosome(new CharSeq(allels), 18)
        );
        /*
         * Create the evolution engine
         */
        Engine<CharacterGene, Double> engine = Engine
                .builder(JeneticsExample::fitness, genotype)
                .populationSize(100)
                .alterers(new SinglePointCrossover<>(1), new Mutator<>(0.01))
                .selector(new RouletteWheelSelector<>())
                .build();
        /*
         * Evolve the population until the solution is found. Take notice of double precision.
         */
        engine.stream()
                .limit(Limits.byFitnessThreshold(0.9999999d))
                .peek(g -> System.out.println("Generation: " + g.getGeneration() + " Best: " + g.getBestPhenotype()))
                .collect(Collectors.toList());
    }

    /**
     * Computes the fitness of a solution based on the number of correct characters
     *
     * @param individual The individual to compute the fitness for
     *
     * @return The fitness
     */
    private static Double fitness(final Genotype<CharacterGene> individual) {
        int fitness = 0;
        Chromosome<CharacterGene> chromosome = individual.getChromosome(0);
        for (int i = 0; i < individual.getChromosome(0).length(); i++) {
            if (chromosome.getGene(i).getAllele() == target.charAt(i)) {
                fitness++;
            }
        }
        return (double) fitness / target.length();
    }
}
