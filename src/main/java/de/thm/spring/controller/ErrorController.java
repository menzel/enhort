package de.thm.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Michael Menzel on 8/2/16.
 */
@Controller
public class ErrorController {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(Model model) {

        model.addAttribute("error_message", "404");
        return "404";
    }

    private class ResourceNotFoundException extends RuntimeException{
    }
}
