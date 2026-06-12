package common.interaction;

import common.commands.Command;
import common.models.Vehicle;

import java.io.Serializable;

public class Request implements Serializable {
    private final Command commandRequest;

    public Request(Command commandRequest) {
        this.commandRequest = commandRequest;
    }
    public Command getCommandRequest() {
        return commandRequest;
    }
}
