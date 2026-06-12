package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import server.utility.CollectionManager;
import common.utility.ResponseBuilder;


public class PrintFieldDescendingNumberOfWheelsCommand extends AbstractCommand{
    private String argument;
    private CollectionManager collectionManager;


    
    public PrintFieldDescendingNumberOfWheelsCommand(String argument) {
        super("print_field_descending_number_of_wheels","вывести значения поля numberOfWheels всех элементов в порядке убывания");
        this.argument=argument;
    }

    public PrintFieldDescendingNumberOfWheelsCommand() {
        super("print_field_descending_number_of_wheels","вывести значения поля numberOfWheels всех элементов в порядке убывания");
    }

    public PrintFieldDescendingNumberOfWheelsCommand(CollectionManager collectionManager) {
        super("print_field_descending_number_of_wheels","вывести значения поля numberOfWheels всех элементов в порядке убывания");
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

    public ExitCodeCommand execute(){
        ExitCodeCommand valid  = validate();
        if (!valid.equals(ExitCodeCommand.OK)){
            return valid;
        }
        try{
            if (collectionManager.getCollection().size()==0){
                throw new WrongAmountOfElementsException("Коллекция пуста!");
            }
            collectionManager.printDescendingNumberOfWheels();
            return ExitCodeCommand.OK;
        }
        catch (WrongAmountOfElementsException e){
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.OK;
        }
        catch (IndexOutOfBoundsException e){
            ResponseBuilder.appendLn("Коллекция пуста!");
            return ExitCodeCommand.OK;
        }
    }

    public ExitCodeCommand validate(){
        boolean isEmptyCollection = false;
        try{
            if (!argument.isEmpty()){
                throw new WrongAmountOfElementsException(getNameOfCommand() + " не принимает аргументов!");
            }
            return ExitCodeCommand.OK;
        }
        catch (WrongAmountOfElementsException e){
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
             return ExitCodeCommand.ERROR;
        }
    }
}