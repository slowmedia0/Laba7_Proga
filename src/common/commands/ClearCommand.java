package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import server.utility.CollectionManager;
import common.utility.ResponseBuilder;

import java.util.regex.Pattern;

public class ClearCommand extends AbstractCommand {
    private CollectionManager collectionManager;
    private String argument;

    
    public ClearCommand(String argument) {
        super("clear","очистить коллекцию");
        this.argument=argument;
    }

    public ClearCommand() {
        super("clear","очистить коллекцию");
    }

    public ClearCommand(CollectionManager collectionManager) {
        super("clear","очистить коллекцию");
        this.collectionManager = collectionManager;
    }
    @Override
    public String getArgument() {
        return argument;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public ExitCodeCommand execute() {
        ExitCodeCommand valid  = validate();
        if (!valid.equals(ExitCodeCommand.OK)){
            return valid;
        }
        collectionManager.clearCollection();
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