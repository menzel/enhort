package de.thm.exception;

import de.thm.spring.backend.StatisticsCollector;

/**
 * Created by Michael Menzel on 15/2/16.
 */
public class TooManyCovariantsException extends Exception{

    public TooManyCovariantsException() {
        StatisticsCollector.getInstance().addErrorC();
    }

    public TooManyCovariantsException(String message) {
        super(message);
        StatisticsCollector.getInstance().addErrorC();
    }
}
