package de.thm.calc;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Result {

    private final int a;
    private final int b;

    public Result(int a, int b) {
        this.a = a;
        this.b = b;
    }


    @Override
    public String toString() {
        return "{" + a + "," + b + '}';
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}

