// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.spring.controller;

import de.thm.command.ExpressionCommand;
import de.thm.command.InterfaceCommand;
import de.thm.monitoring.Monitor;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Application Controller for default routing
 *
 * Created by Michael Menzel on 22/2/16.
 */
@Controller
public class ApplicationController implements ErrorController {

    /**
     * Index/ Welcome page with four links
     *
     * @return index page
     */
    @RequestMapping(value = {"/welcome", "/"}, method = RequestMethod.GET)
    public String index(HttpSession session) {

        clear_session(session);

        return "index";
    }

    @RequestMapping(value = "/clear_session", method = RequestMethod.GET)
    public String deleteSession(Model model, HttpSession session) {

        clear_session(session);

        InterfaceCommand command = new InterfaceCommand();
        command.setOriginalFilename("");
        command.setMinBg(10000);

        model.addAttribute("interfaceCommand", command);
        model.addAttribute("expressionCommand", new ExpressionCommand());

        return "index";
    }


    private void clear_session(HttpSession session){

        Sessions sessionsControl = Sessions.getInstance();
        sessionsControl.getSession(session.getId()).getConnector().close();
        sessionsControl.clear(session.getId());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }


    // Error page
    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            if (Integer.valueOf(status.toString()) == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            }
        }

        model.addAttribute("errorCode", request.getAttribute("javax.servlet.error.status_code"));
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String errorMessage = "";

        StatisticsCollector.getInstance().addErrorC();

        if (throwable != null) {
            errorMessage = throwable.getMessage();
        }

        if (errorMessage.equals("null") || errorMessage.length() == 0 && throwable != null) {
            errorMessage = throwable.getClass().getCanonicalName();
        }

        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    @ControllerAdvice
    public class FileErrorController extends ResponseEntityExceptionHandler {

        @ExceptionHandler(MultipartException.class)
        String handleFileException(HttpServletRequest request, Throwable ex, Model model) {
            model.addAttribute("errorMessage", "The file you tried to upload is too large. Please upload only files with a max. file size of 20 MB.");
            return "error";
        }
    }

    @RequestMapping("/faq")
    public String faq(){

        return "faq";
    }

    @RequestMapping("/tutorial")
    public String tutorial() {

        return "tutorial";
    }
    @RequestMapping("/contact")
    public String contact(){

        return "contact";
    }

    @RequestMapping("/status")
    public ResponseEntity monitor() {

        if (Monitor.isConnectionAlive())
            return new ResponseEntity(HttpStatus.valueOf(200));
        return new ResponseEntity(HttpStatus.valueOf(500));

    }



}
