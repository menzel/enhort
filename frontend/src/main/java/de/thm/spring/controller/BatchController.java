package de.thm.spring.controller;

import de.thm.command.BackendCommand;
import de.thm.command.Command;
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

@Controller
public class BatchController {

    /**
     * Generates a pair of effect size and a * for the pvalue significance for the heatmap plot.
     * Effect size is set to negative if less sites are measured inside as expected
     *
     * @param test -test result for one track
     * @return pair of effect size and *** for p value to plot in heatmap
     */
    private Pair<Double, String> getHeatmapPair(TestResult test) {
        double pval = 0.05;

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
    public String runBatch(Model model, @RequestParam("file[]") List<MultipartFile> files, @RequestParam("background") MultipartFile bg, HttpSession httpSession) {

        List<Sites> batchSites = new ArrayList<>();
        Sessions sessionControll = Sessions.getInstance();
        Session currentSession = sessionControll.addSession(httpSession.getId());
        List<String> names = new ArrayList<>();
        Sites background;
        BackendCommand command;

        Comparator<TestResult> byTrack = Comparator.comparingInt(t -> t.getTrack().getUid());
        Comparator<TestResult> byPackage = Comparator.comparing(t -> t.getTrack().getPack());

        for (MultipartFile mf : files) {
            batchSites.add(ControllerHelper.getUserData(mf));
            names.add(mf.getOriginalFilename());
        }

        if (!Objects.isNull(bg)) {
            background = ControllerHelper.getUserData(bg);
            command = new BackendCommand.Builder(Command.Task.ANALYZE_BATCH, batchSites.get(0).getAssembly()).batchSites(batchSites).sitesBg(background).build();

        } else
            command = new BackendCommand.Builder(Command.Task.ANALYZE_BATCH, batchSites.get(0).getAssembly()).batchSites(batchSites).build();

        try {
            /////////// Run analysis ////////////
            BatchResult batch = (BatchResult) currentSession.getConnector().runAnalysis(command);
            /////////////////////////////////////


            SortedMap<String, List<Pair<Double, String>>> results = new TreeMap<>(); // effect size, p value

            int i = 0;
            for (Result c : batch.getResults()) {

                List<Pair<Double, String>> tmp = ((ResultCollector) c).getResults().stream()
                        .sorted(byTrack)
                        .map(this::getHeatmapPair)
                        .collect(Collectors.toList());

                results.put(names.get(i++), tmp);
            }

            model.addAttribute("results", results);
            model.addAttribute("ran", true);
            model.addAttribute("tracks", ((ResultCollector) batch.getResults().get(0)).getResults().stream()
                    .sorted(byTrack)
                    .map(r -> r.getTrack().getName())
                    .collect(Collectors.toList()));


        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }

        return "batch";
    }
}
