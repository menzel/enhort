package de.thm.exception;

/**
 * Created by Michael Menzel on 15/2/16.
 */
public class TooManyCovariantsException extends Exception{

    public TooManyCovariantsException() { }

    public TooManyCovariantsException(String message) {
        super(message);
    }
}
