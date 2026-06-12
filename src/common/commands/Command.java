package common.commands;

import common.ExitCodeCommand;

public interface Command {
    ExitCodeCommand execute();
    String getNameOfCommand();
    String getDescriptionOfCommand();
    String getArgument();
    ExitCodeCommand validate();
}