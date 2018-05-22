package de.thm.command;

import de.thm.genomeData.tracks.SerializeableInOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Backend command object to send data from interface to backend.
 * BackendCommand is immutable.
 *
 * Is build using the builder pattern, example:
 * new BackendCommand.Builder(Command.Task.ANALZYE_SINGLE, currentSession.getSites().getAssembly()).sites(sites).sitesBg(sitesBg).build();
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendCommand implements Command {
    private final long serialVersionUID = 917124312121L;

    private final List<String> covariants; //list of ids of tracks that are used as covariant
    private final int minBg; //minimum of expected background positions
    private final List<SerializeableInOutTrack> customTracks;
    private final Sites sites;
    private final Sites sitesBg;
    private final Genome.Assembly assembly;
    private final boolean logoCovariate;
    private final boolean createLogo;
    private final List<String> tracks;
    private final List<Sites> batchSites;
    private final Task task;
    private final List<String> packages;
    private final String cellline;

    /**
     * Build using the builder pattern
     *
     * @param builder - builder object
     */
    private BackendCommand(Builder builder) {
        this.covariants = builder.covariants;
        this.minBg = builder.minBg;
        this.customTracks = builder.customTracks;
        this.sites = builder.sites;
        this.sitesBg = builder.sitesBg;
        this.assembly = builder.assembly;
        this.logoCovariate = builder.logoCovariate;
        this.createLogo = builder.createLogo;
        this.tracks = builder.tracks;
        this.batchSites = builder.batchSites;
        this.task = builder.task;
        this.packages = builder.packages;
        this.cellline = builder.cellline;
    }

    public void addCustomTrack(List<SerializeableInOutTrack> track) {
        this.customTracks.addAll(track);
    }


    /**
     * Constructor to get all tracks from bg.
     * Used by the data table view
     *
     * @param assembly - assembly number to get
     */
    public BackendCommand(Genome.Assembly assembly, Task task) {
        this.assembly = assembly;

        this.covariants = new ArrayList<>();
        this.sites = null;
        this.minBg = 0;
        this.customTracks = new ArrayList<>();
        this.logoCovariate = false;
        this.createLogo = false;
        this.sitesBg = null;
        this.tracks = Collections.emptyList();
        this.task = task;
        this.batchSites = null;
        this.packages = null;
        this.cellline = null;
    }

    public List<Track> getCustomTracks() {
        return customTracks.stream().map(SerializeableInOutTrack::getInOut).collect(Collectors.toList());
    }

    public List<String> getPackages() {
        return packages;
    }

    public List<String> getCovariants() {
        return covariants;
    }

    public Sites getSites() {
        return sites;
    }

    public int getMinBg() {
        return minBg;
    }

    public String getCellline() {
        return cellline;
    }


    public Genome.Assembly getAssembly() {
        return assembly;
    }

    public boolean isLogoCovariate() {
        return logoCovariate;
    }

    public boolean isCreateLogo() {
        return createLogo;
    }

    public Sites getSitesBg() {
        return sitesBg;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public Task getTask() {
        return task;
    }

    public List<Sites> getBatchSites() {
        return batchSites;
    }

    /**
     * Inner class for builder pattern
     */
    public static class Builder {
        // Required params
        private final Genome.Assembly assembly;
        private final Task task;


        // Optional params
        private List<String> covariants = Collections.emptyList(); //list of ids of tracks that are used as covariant
        private int minBg = 10000; //minimum of expected background positions
        private List<SerializeableInOutTrack> customTracks = Collections.emptyList();
        private Sites sites = null;
        private Sites sitesBg = null;
        private boolean logoCovariate = false;
        private boolean createLogo = false;
        private List<String> tracks = Collections.emptyList();
        private List<Sites> batchSites = Collections.emptyList();
        private List<String> packages;
        private String cellline;


        public Builder(Task task, Genome.Assembly assembly) {

            this.task = task;
            this.assembly = assembly;
        }

        public BackendCommand build() {
            check();

            return new BackendCommand(this);
        }

        /**
         * Makes a sanity check on the configuration.
         *
         * @throws RuntimeException - if anything is missing or not working in this combination
         */
        private void check() throws RuntimeException {

            if (task == Task.ANALZYE_SINGLE) {
                if (Objects.isNull(sites))
                    throw new RuntimeException("No sites supplied for backend command");
            }


            if (task == Task.ANALYZE_BATCH) {
                if (batchSites.size() == 0)
                    throw new RuntimeException("No batch sites supplied for backend command");
                if (Objects.nonNull(sites))
                    throw new RuntimeException("Additional sites added in backend command for batch analysis");
            }
        }


        public Builder covariants(List<String> val) {
            this.covariants = val;
            return this;
        }

        public Builder minBg(int val) {
            this.minBg = val;
            return this;
        }

        public Builder customTracks(List<SerializeableInOutTrack> val) {
            this.customTracks = val;
            return this;
        }

        public Builder sites(Sites val) {
            this.sites = val;
            return this;
        }

        public Builder sitesBg(Sites val) {
            this.sitesBg = val;
            return this;
        }

        public Builder packages(List<String> packages) {
            this.packages = packages;
            return this;
        }

        public Builder cellline(String cellline) {
            this.cellline = cellline;
            return this;
        }

        public Builder logoCovariate(boolean val) {
            this.logoCovariate = val;
            return this;
        }


        public Builder createLogo(boolean val) {
            this.createLogo = val;
            return this;
        }

        public Builder tracks(List<String> val) {
            this.tracks = val;
            return this;
        }

        public Builder batchSites(List<Sites> val) {
            this.batchSites = val;
            return this;
        }
    }
}
