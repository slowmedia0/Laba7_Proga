package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import server.utility.CollectionManager;
import common.utility.ResponseBuilder;


public class SumOfEnginePowerCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private String argument;

    
    public SumOfEnginePowerCommand(String argument) {
        super("sum_of_engine_power","вывести сумму значений поля enginePower для всех элементов коллекции");
        this.argument=argument;
    }

    public SumOfEnginePowerCommand() {
        super("sum_of_engine_power","вывести сумму значений поля enginePower для всех элементов коллекции");
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public SumOfEnginePowerCommand(CollectionManager collectionManager) {
        super("sum_of_engine_power","вывести сумму значений поля enginePower для всех элементов коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public ExitCodeCommand execute(){
        ExitCodeCommand valid = validate();
        if (!valid.equals(ExitCodeCommand.OK)) {
            return valid;
        }
        try{
            if (collectionManager.getCollection().size()==0){
                throw new WrongAmountOfElementsException("Коллекция пуста!");
            }
            collectionManager.sumEnginePower();
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