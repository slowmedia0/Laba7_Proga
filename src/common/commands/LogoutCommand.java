package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.ResponseBuilder;
import server.utility.CollectionManager;

public class LogoutCommand extends AbstractCommand {
    private String argument;

    public LogoutCommand(String argument) {
        super("logout", "выйти из текущего аккаунта");
        this.argument = argument;
    }

    public LogoutCommand() {
        super("logout", "выйти из текущего аккаунта");
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    @Override
    public ExitCodeCommand execute() {
        ExitCodeCommand valid = validate();
        if (!valid.equals(ExitCodeCommand.OK)) {
            return valid;
        }

        CollectionManager.setCurrentUser(null);
        ResponseBuilder.append("✅ Вы успешно вышли из аккаунта.");
        return ExitCodeCommand.OK;
    }

    @Override
    public ExitCodeCommand validate() {
        try {
            if (!argument.isEmpty()) {
                throw new WrongAmountOfElementsException("Команда logout не принимает аргументов!");
            }
            return ExitCodeCommand.OK;
        } catch (WrongAmountOfElementsException e) {
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }
}