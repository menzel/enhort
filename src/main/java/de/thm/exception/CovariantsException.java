package de.thm.exception;

import de.thm.spring.StatisticsCollector;
import de.thm.spring.serverStatistics.StatisticsCollector;

/**
 * Created by Michael Menzel on 15/2/16.
 */
public final class CovariantsException extends Exception {

    public CovariantsException() {
        StatisticsCollector.getInstance().addErrorC();
    }

    public CovariantsException(String message) {
        super(message);
        StatisticsCollector.getInstance().addErrorC();
    }
}
