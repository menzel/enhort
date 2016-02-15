package de.thm.exception;

import de.thm.spring.backend.StatisticsCollector;

/**
 * Created by Michael Menzel on 15/2/16.
 */
public class IntervalTypeNotAllowedExcpetion extends Throwable {

    public IntervalTypeNotAllowedExcpetion(String s) {
        super(s);
        StatisticsCollector.getInstance().addErrorC();
    }
}
