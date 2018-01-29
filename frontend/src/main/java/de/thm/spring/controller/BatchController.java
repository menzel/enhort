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

    @RequestMapping(value = {"/batch"}, method = RequestMethod.GET)
    public String index() {
        return "batch";
    }

    @RequestMapping(value = {"/batch"}, method = RequestMethod.POST)
    public String runBatch(Model model, @RequestParam("file[]") List<MultipartFile> files, HttpSession httpSession) {

        List<Sites> batchSites = new ArrayList<>();
        Sessions sessionControll = Sessions.getInstance();
        Session currentSession = sessionControll.addSession(httpSession.getId());
        List<String> names = new ArrayList<>();
        double pval = 0.05;

        Comparator<TestResult> byTrack = Comparator.comparingInt(t -> t.getTrack().getUid());

        for (MultipartFile mf : files) {
            batchSites.add(ControllerHelper.getUserData(mf));
            names.add(mf.getOriginalFilename());
        }

        BackendCommand command = new BackendCommand(batchSites, Command.Task.ANALYZE_BATCH);

        try {
            /////////// Run analysis ////////////
            BatchResult batch = (BatchResult) currentSession.getConnector().runAnalysis(command);
            /////////////////////////////////////

            Map<String, List<Pair<Double, String>>> results = new HashMap<>(); // effect size, p value

            int i = 0;
            for (Result c : batch.getResults()) {

                List<Pair<Double, String>> tmp = ((ResultCollector) c).getResults().stream()
                        .sorted(byTrack)
                        .map(track -> new ImmutablePair<>(track.getEffectSize(), track.getpValue() < pval ? "*" : ""))
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
