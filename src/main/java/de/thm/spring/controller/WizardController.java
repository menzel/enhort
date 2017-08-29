package de.thm.spring.controller;

import de.thm.exception.CovariantsException;
import de.thm.exception.NoTracksLeftException;
import de.thm.guess.AssemblyGuesser;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.BackendConnector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.InterfaceCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.util.UUID;


@Controller
public class WizardController {

    private static final Path basePath = new File("/tmp").toPath();


    @RequestMapping(value = "/wiz", method = RequestMethod.GET)
    public String wizard(Model model, HttpSession httpSession){

        model.addAttribute("page", "upload");

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());
        currentSession.setSites(null); // reset old session


        InterfaceCommand command = new InterfaceCommand();
        model.addAttribute("interfaceCommand", command);

        return "wizard";
    }


    @RequestMapping(value = "/wiz", method = RequestMethod.POST)
    public String wizard2(Model model, HttpSession httpSession, @RequestParam("file") MultipartFile file, @ModelAttribute InterfaceCommand interfaceCommand){

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        if(currentSession.getSites() == null) {

            String name = file.getOriginalFilename();
            String uuid = name + "-" + UUID.randomUUID();

            Path inputFilepath = null;

            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(uuid).toFile()));
                    stream.write(bytes);
                    stream.close();

                    inputFilepath = basePath.resolve(uuid);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                model.addAttribute("message", "Upload failed. The file was empty.");
                return "error";
            }

            UserData data = new UserData(AssemblyGuesser.guessAssembly(inputFilepath),inputFilepath);
            currentSession.setSites(data);
            currentSession.setOriginalFilename(file.getOriginalFilename());
            interfaceCommand.setAssembly(data.getAssembly().toString());

            BackendCommand command = new BackendCommand(data);

            ResultCollector collector = null;
            try {
                collector = (ResultCollector) BackendConnector.getInstance().runAnalysis(command);
            } catch (NoTracksLeftException | CovariantsException | SocketTimeoutException e) {
                e.printStackTrace();
            }

            if (collector == null) {
                model.addAttribute("errorMessage", "Backend Connection Error");
                return "error";
            }

            currentSession.setCollector(collector);

            model.addAttribute("interfaceCommand", interfaceCommand);
            model.addAttribute("trackPackages", collector.getKnownPackages());
            model.addAttribute("celllines", collector.getKnownCelllines());

            model.addAttribute("page", "packages");
            return "wizard";

        } else if(interfaceCommand.getTracks().size() > 0) { // serve covariates page:

            interfaceCommand.setAssembly(currentSession.getSites().getAssembly().toString());
            interfaceCommand.setSites(currentSession.getSites());
            interfaceCommand.setPositionCount(currentSession.getSites().getPositionCount());

            BackendCommand command = new BackendCommand(interfaceCommand);

            ResultCollector collector = null;
            try {
                collector = (ResultCollector) BackendConnector.getInstance().runAnalysis(command);

            } catch(NoTracksLeftException e){

                model.addAttribute("interfaceCommand", interfaceCommand);
                model.addAttribute("trackPackages", currentSession.getCollector().getKnownPackages());
                model.addAttribute("celllines", currentSession.getCollector().getKnownCelllines());
                model.addAttribute("page", "packages");
                model.addAttribute("message", "There are no tracks for this combination of cell lines and packages");

                return "wizard";
            } catch (CovariantsException | SocketTimeoutException e){
                e.printStackTrace();
            }

            if (collector == null) {
                model.addAttribute("errorMessage", "Backend Connection Error");
                return "error";
            }

            currentSession.setCollector(collector);

            model.addAttribute("interfaceCommand", interfaceCommand);
            model.addAttribute("tracks", currentSession.getCollector().getInOutResults(false));
            model.addAttribute("page", "covariates");

            //TODO

            return "wizard";

        }
        return "error";
    }
}
