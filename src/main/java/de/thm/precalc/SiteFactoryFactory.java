package de.thm.precalc;

import de.thm.logo.GenomeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for PrecalcSitesFactory to store factory for different genomes
 *
 * Created by menzel on 2/13/17.
 */
public final class SiteFactoryFactory {
    private static SiteFactoryFactory instance;
    private final Map<GenomeFactory.Assembly, SiteFactory> factories;

    private SiteFactoryFactory(){
        factories = new HashMap<>();
        factories.put(GenomeFactory.Assembly.hg19, new SiteFactory(GenomeFactory.Assembly.hg19, 100000));
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
    public SiteFactory get(GenomeFactory.Assembly assembly) {
        return this.factories.get(assembly);
    }
}
