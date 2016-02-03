package de.thm.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@Controller
public class CovariantController {

    @RequestMapping(value="/covariant", method= RequestMethod.POST)
    public String handleFileUpload(Model model){

        //TODO get covariants and run again
        return "works";
    }
}
