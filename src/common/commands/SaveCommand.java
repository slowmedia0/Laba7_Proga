package common.commands;

import common.ExitCodeCommand;
import common.utility.ResponseBuilder;
import server.utility.CollectionManager;

public class SaveCommand extends AbstractCommand {

    private final CollectionManager collectionManager;

    public SaveCommand(CollectionManager collectionManager) {
        super("save", "сохранить коллекцию в базу данных");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExitCodeCommand execute() {
        boolean success = collectionManager.saveToDatabase();
        if (success) {
            ResponseBuilder.append(" Коллекция успешно сохранена в базу данных.");
            System.out.println(" Коллекция успешно сохранена в БД.");
        } else {
            ResponseBuilder.append(" Не удалось сохранить коллекцию в БД.");
            System.out.println(" Не удалось сохранить коллекцию в БД.");
        }
        return ExitCodeCommand.OK;
    }

    @Override
    public String getArgument() {
        return "";
    }

    @Override
    public ExitCodeCommand validate() {
        return ExitCodeCommand.OK;
    }
}