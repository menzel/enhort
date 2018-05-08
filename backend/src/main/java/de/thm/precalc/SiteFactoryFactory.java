package de.thm.precalc;

import de.thm.misc.Genome;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for PrecalcSitesFactory to store factory for different genomes
 *
 * Created by menzel on 2/13/17.
 */
public final class SiteFactoryFactory {
    private static SiteFactoryFactory instance;
    private final Map<Genome.Assembly, SiteFactory> factories;

    private SiteFactoryFactory(){
        factories = new HashMap<>();

        int n;
        if (System.getenv("HOME").contains("menzel")) {
            n = 100000;
        } else {
            n = 3 * 1000 * 1000;
        }

        factories.put(Genome.Assembly.hg19, new SiteFactory(Genome.Assembly.hg19, n));
    }

    public static SiteFactoryFactory getInstance(){

        if (instance == null) {
            instance = new SiteFactoryFactory();
        }
        return instance;
    }

    /**
     * Returns a factory by given assembly
     *
     * @param assembly nr to get
     *
     * @return factory of given assembly
     */
    public SiteFactory get(Genome.Assembly assembly) throws RuntimeException{
        if(this.factories.containsKey(assembly))
            return this.factories.get(assembly);
        throw  new RuntimeException("SiteFactoryFactory: There is no factory for " + assembly);
    }
}
