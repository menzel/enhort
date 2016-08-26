package de.thm.exception;

/**
 * Exception to handle invalid track types for bg models which only allow certain tracks
 *
 * Created by Michael Menzel on 15/2/16.
 */
public final class IntervalTypeNotAllowedExcpetion extends Throwable {

    public IntervalTypeNotAllowedExcpetion(String s) {
        super(s);
    }
}
