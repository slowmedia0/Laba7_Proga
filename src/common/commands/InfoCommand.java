package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import server.utility.CollectionManager;
import common.utility.ResponseBuilder;


public class InfoCommand extends AbstractCommand{
    private String argument;
    private CollectionManager collectionManager;

    
    public InfoCommand(String argument) {
        super("info","вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.argument=argument;
    }

    public InfoCommand(CollectionManager collectionManager) {
        super("info","вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.collectionManager = collectionManager;
    }

    public InfoCommand() {
        super("info","вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
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

    public ExitCodeCommand execute(){
        ExitCodeCommand valid  = validate();
        if (!valid.equals(ExitCodeCommand.OK)){
            return valid;
        }
        collectionManager.infoAboutCollection();
        return ExitCodeCommand.OK;
    }

    public ExitCodeCommand validate(){
        try{
            if (!argument.isEmpty()){
                throw new WrongAmountOfElementsException(getNameOfCommand() + " не принимает аргументов!");
            }
            return ExitCodeCommand.OK;
        } catch (WrongAmountOfElementsException e){
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }

}