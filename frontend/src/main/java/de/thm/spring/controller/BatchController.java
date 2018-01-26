package de.thm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@Controller
public class BatchController {

    @RequestMapping(value = {"/batch"}, method = RequestMethod.GET)
    public String index() {
        return "batch";
    }


    @RequestMapping(value = {"/batch"}, method = RequestMethod.POST)
    public String runBatch(Model model, @RequestParam("file") MultipartFile file, HttpSession httpSession) {


        return "batch";
    }


}
