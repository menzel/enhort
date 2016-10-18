package de.thm.spring.command;

import java.io.Serializable;

/**
 * Created by menzel on 10/18/16.
 */
public final class ExpressionCommand implements Serializable, Command{
    private String expression = "";

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

}
