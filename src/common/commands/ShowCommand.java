package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.ResponseBuilder;
import server.utility.CollectionManager;

public class ShowCommand extends AbstractCommand {
    private CollectionManager collectionManager;
    private String argument;

    public ShowCommand(String argument) {
        super("show", "вывести в стандартный поток вывода элементы коллекции в строковом представлении");
        this.argument = argument;
    }

    public ShowCommand() {
        super("show", "вывести в стандартный поток вывода элементы коллекции в строковом представлении");
    }

    public ShowCommand(CollectionManager collectionManager) {
        super("show [номер_страницы]", "вывести в стандартный поток вывода элементы коллекции в строковом представлении");
        this.collectionManager = collectionManager;
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
    public ExitCodeCommand validate() {
        
        try {
            if (!argument.isEmpty()) {
                try {
                    int page = Integer.parseInt(argument.trim());
                    if (page < 1) {
                        throw new WrongAmountOfElementsException("Номер страницы должен быть больше 0");
                    }
                } catch (NumberFormatException e) {
                    throw new WrongAmountOfElementsException("Номер страницы должен быть целым числом");
                }
            }
            return ExitCodeCommand.OK;
        } catch (WrongAmountOfElementsException e) {
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }

    @Override
    public ExitCodeCommand execute() {
        ExitCodeCommand valid = validate();
        if (!valid.equals(ExitCodeCommand.OK)) {
            return valid;
        }

        try {
            if (collectionManager.getCollection().size() == 0) {
                throw new WrongAmountOfElementsException("Коллекция пуста!");
            }

            
            String output = collectionManager.show(argument);
            ResponseBuilder.append(output);
            return ExitCodeCommand.OK;

        } catch (WrongAmountOfElementsException e) {
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.OK;
        } catch (Exception e) {
            ResponseBuilder.appendLn("Ошибка при выводе коллекции: " + e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }
}