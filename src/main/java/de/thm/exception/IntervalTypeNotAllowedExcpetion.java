package de.thm.exception;

import de.thm.serverStatistics.StatisticsCollector;

/**
 * Created by Michael Menzel on 15/2/16.
 */
public final class IntervalTypeNotAllowedExcpetion extends Throwable {

    public IntervalTypeNotAllowedExcpetion(String s) {
        super(s);
        StatisticsCollector.getInstance().addErrorC();
    }
}
