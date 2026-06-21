package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.ResponseBuilder;
import server.utility.CollectionManager;

public class ExitCommand extends AbstractCommand {
    private CollectionManager collectionManager;
    private String argument;

    public ExitCommand(String argument) {
        super("exit", "завершить программу (с сохранением в БД)");
        this.argument = argument;
    }

    public ExitCommand() {
        super("exit", "завершить программу (с сохранением в БД)");
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
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

        
        if (collectionManager != null) {
            System.out.println(" Выполняется сохранение коллекции в базу данных...");
            SaveCommand saveCommand = new SaveCommand(collectionManager);
            saveCommand.execute();
        } else {
            System.out.println(" Warning: collectionManager is null in ExitCommand");
        }

        ResponseBuilder.append("Клиент завершает работу.");
        return ExitCodeCommand.EXIT;
    }

    @Override
    public ExitCodeCommand validate() {
        try {
            if (!argument.isEmpty()) {
                throw new WrongAmountOfElementsException("Команда exit не принимает аргументов!");
            }
            return ExitCodeCommand.OK;
        } catch (WrongAmountOfElementsException e) {
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }
}