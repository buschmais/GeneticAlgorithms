package com.buschmais.javaspektrum.geneticalgorithms.resourceplanning.singleobjective;

/**
 * Class representing a resource which executes tasks
 *
 * @author Stephan Pirnbaum
 */
public class Resource {

    /**
     * The costs per minute for this resource
     */
    private final double costsPerMinute;

    /**
     * The items per minute produced by this resource
     */
    private final long itemsPerMinute;

    /**
     * Creates a resource
     *
     * @param costsPerMinute The costs per minute for this resource
     *
     * @param itemsPerMinute The items per hour produced by this resource
     */
    Resource(double costsPerMinute, long itemsPerMinute) {
        this.costsPerMinute = costsPerMinute;
        this.itemsPerMinute = itemsPerMinute;
    }

    /**
     * The costs per minute for this resource
     *
     * @return The costs per minute
     */
    double getCostsPerMinute() {
        return costsPerMinute;
    }

    /**
     * The items per minute produced by this resource
     *
     * @return The items per minute
     */
    long getItemsPerMinute() {
        return itemsPerMinute;
    }

    @Override
    public String toString() {
        return "Resource[costsPerMinute: " + costsPerMinute + " itemsPerMinute: " + itemsPerMinute + "]";
    }
}
