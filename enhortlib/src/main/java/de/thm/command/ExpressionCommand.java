package de.thm.command;

/**
 * Created by menzel on 10/18/16.
 */
public final class ExpressionCommand implements Command {
    private String expression = "";

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

}
