package de.thm.monitoring;

import de.thm.command.BackendCommand;
import de.thm.command.Command;
import de.thm.exception.CovariatesException;
import de.thm.exception.NoTracksLeftException;
import de.thm.misc.Genome;
import de.thm.result.DataViewResult;
import de.thm.spring.backend.Sessions;

import java.net.SocketTimeoutException;

public class Monitor {

    /**
     * Checks the connection to the backend server  by sending a request
     *
     * @return false if connection is not present ; true if connections works
     */
    public static boolean isConnectionAlive() {

        BackendCommand command = new BackendCommand.Builder(Command.Task.GET_TRACKS, Genome.Assembly.hg19).build();

        try {
            DataViewResult collector = (DataViewResult) Sessions.getInstance().getSession("monitorsession").getConnector().runAnalysis(command);

            if (collector == null || collector.getPackages().size() < 1) {
                return false;
            }

        } catch (SocketTimeoutException | CovariatesException | NoTracksLeftException e) {
            return false;
        }

        return true;
    }
}
