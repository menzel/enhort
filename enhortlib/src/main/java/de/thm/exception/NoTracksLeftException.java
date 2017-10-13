package de.thm.exception;

/**
 * Exception class for exceptions where not track or site is left due to restictive settings
 */
public class NoTracksLeftException extends Exception{
    public NoTracksLeftException(String s) {
        super(s);
    }
}
