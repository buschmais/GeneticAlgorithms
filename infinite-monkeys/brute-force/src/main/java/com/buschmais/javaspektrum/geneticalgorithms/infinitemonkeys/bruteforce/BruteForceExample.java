package com.buschmais.javaspektrum.geneticalgorithms.infinitemonkeys.bruteforce;

import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.IntStream.*;
import static java.util.stream.IntStream.concat;

/**
 * Brute force implementation of the infinite monkeys theorem. The code aims to create the string "to be or not to be".
 *
 * @author Stephan Pirnbaum
 */
public class BruteForceExample {

    public static void main(String[] args) {
        final String target = "to be or not to be";
        final int[] allels = concat(of(32), range(97, 123)).toArray();

        final StringBuilder proposal = new StringBuilder();
        long cnt = 0;
        do {
            cnt++;
            proposal.setLength(0);
            range(0, target.length()).forEach(i ->
                    proposal.append((char) allels[ThreadLocalRandom.current().nextInt(allels.length)])
            );
            if (cnt % 10000 == 0) System.out.println("Iteration: " + cnt);
        } while (!target.equals(proposal.toString()));
        System.out.println("Solution found after " + cnt + " iterations!");
    }

}
