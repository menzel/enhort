package de.thm.spring.controller;

import de.thm.command.BackendCommand;
import de.thm.command.Command;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import de.thm.result.BatchResult;
import de.thm.result.Result;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.stat.TestResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class BatchController {

    /**
     * Generates a pair of effect size and a * for the pvalue significance for the heatmap plot.
     * Effect size is set to negative if less sites are measured inside as expected
     *
     * @param test -test result for one track
     * @return pair of effect size and *** for p value to plot in heatmap
     */
    private Pair<Double, String> getHeatmapPair(TestResult test, int n) {
        double pval = 0.05 / n;

        Double left;
        if (test.getPercentInM() < test.getPercentInE())
            left = -1 * test.getEffectSize();
        else left = test.getEffectSize();

        String right = test.getpValue() < pval ? test.getpValue() < pval / 5 ? "**" : "*" : "";

        return new ImmutablePair<>(left, right);
    }


    @RequestMapping(value = {"/batch"}, method = RequestMethod.GET)
    public String index() {
        return "batch";
    }

    @RequestMapping(value = {"/batch"}, method = RequestMethod.POST)
    public String runBatch(Model model,
                           @RequestParam("file[]") List<MultipartFile> files,
                           @RequestParam("background") MultipartFile bg,
                           @RequestParam("package") List<String> packages,
                           @RequestParam("assembly") String assem,
                           HttpSession httpSession) {

        List<Sites> batchSites = new ArrayList<>();
        Sessions sessionControll = Sessions.getInstance();
        Session currentSession = sessionControll.addSession(httpSession.getId());
        List<String> names = new ArrayList<>();
        Sites background;
        BackendCommand command;
        Genome.Assembly assembly = Genome.Assembly.valueOf(assem);

        if (files.size() > 30) {
            model.addAttribute("errorMessage", "Too many files, please upload not more than 30 .bed-files");
            return "error";
        }

        Comparator<TestResult> byPackage = Comparator.comparing(t -> t.getTrack().getPack());
        Comparator<TestResult> byTrack = byPackage.thenComparing(Comparator.comparing(t -> t.getTrack().getName()));
        Comparator<String> byIndex = Comparator.comparing(names::indexOf); // sort by index of names of user uploaded bed file names

        for (MultipartFile mf : files) {
            batchSites.add(ControllerHelper.getUserData(mf));
            names.add(mf.getOriginalFilename());
        }

        if (!Objects.isNull(bg) && !bg.isEmpty()) {
            background = ControllerHelper.getUserData(bg);
            command = new BackendCommand.Builder(Command.Task.ANALYZE_BATCH, assembly).batchSites(batchSites).packages(packages).sitesBg(background).build();

        } else
            command = new BackendCommand.Builder(Command.Task.ANALYZE_BATCH, assembly).packages(packages).batchSites(batchSites).build();

        try {
            /////////// Run analysis ////////////
            BatchResult batch = (BatchResult) currentSession.getConnector().runAnalysis(command);
            /////////////////////////////////////

            // organize results for display //

            SortedMap<String, List<Pair<Double, String>>> results = new TreeMap<>(byIndex); // map of results for heatmap effect size, p value
            List<List<Integer>> hotspots = new ArrayList<>();

            ResultCollector first = (ResultCollector) Objects.requireNonNull(batch).getResults().get(0);

            // sizes for site count info table
            List<Integer> sizes = batchSites.stream().map(Sites::getPositionCount).collect(Collectors.toList());
            sizes.add(first.getBgCount());

            // integration counts for heatmap hover
            List<List<List<Number>>> integration_counts = new ArrayList<>();
            List<List<Number>> inner = Stream.generate(ArrayList<Number>::new).limit(batch.getResults().size()).collect(Collectors.toList());

            for (int y = 0; y < first.getResults().size(); y++) {
                integration_counts.add(new ArrayList<>(inner));
            }

            int i = 0;
            for (Result c : batch.getResults()) {

                ResultCollector current = (ResultCollector) c;
                List<TestResult> sortedResults = new ArrayList<>(current.getResults());
                sortedResults.sort(byTrack);
                System.out.println(names.get(i));

                for (int trackN = 0; trackN < sortedResults.size(); trackN++) {
                    TestResult tr = sortedResults.get(trackN);
                    List<Number> tmp = new ArrayList<>();


                    System.out.println(tr.getTrack().getName() + " fold change: " + tr.getEffectSize() + " In sites: " + tr.getMeasuredIn() + " In control: " + tr.getExpectedIn()
                            + " Out sites: " + tr.getMeasuredOut() + " Out control: " + tr.getExpectedOut()
                            + " fc: " + tr.getEffectSize());


                    tmp.add(tr.getMeasuredIn());
                    tmp.add(tr.getMeasuredOut());
                    tmp.add(tr.getExpectedIn());
                    tmp.add(tr.getExpectedOut());

                    tmp.add(tr.getPercentInM());
                    tmp.add(tr.getPercentOutM());
                    tmp.add(tr.getPercentInE());
                    tmp.add(tr.getPercentOutE());

                    tmp.add(tr.getpValue());

                    integration_counts.get(trackN) // track number
                            .set(i, tmp); //i  -> file number}
                }
                // end integration counts


                hotspots.add(current.getHotspots());

                List<Pair<Double, String>> tmp = sortedResults.stream()
                        .map(result -> getHeatmapPair(result, names.size()))
                        .collect(Collectors.toList());


                //((ResultCollector) c).getResults();
                results.put(names.get(i++), tmp);
                System.out.println();
            }


            List<String> tracklist = first.getResults().stream()
                    .sorted(byTrack)
                    .map(r -> r.getTrack().getName())
                    .collect(Collectors.toList());


            // add to model
            model.addAttribute("ran", true);
            model.addAttribute("results", results);
            model.addAttribute("tracks", tracklist);
            model.addAttribute("hotspots", hotspots);
            model.addAttribute("names", names);
            model.addAttribute("sizes", sizes);
            model.addAttribute("integration_counts", integration_counts);
            model.addAttribute("bg_site_count", first.getBackgroundSites().getPositionCount());

            ControllerHelper.setHotspotBoundaries(model, batch.getResults().get(0).getAssembly());

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "batch";
    }
}
