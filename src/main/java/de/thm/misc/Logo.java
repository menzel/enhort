package de.thm.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for a sequence logo
 *
 * Created by menzel on 11/9/16.
 */
public final class Logo implements Serializable{

    private final List<Map<String, Double>> heights = new ArrayList<>();

    public void add(Map<String, Double> height) {
        heights.add(height);
    }

    public List<Map<String, Double>> getHeights() {
        return heights;
    }

    @Override
    public String toString() {
        return heights.toString();
    }
}
