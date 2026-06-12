package common.commands;

import common.ExitCodeCommand;
import common.exceptions.ValidateDataException;
import common.exceptions.WrongAmountOfElementsException;
import server.utility.CollectionManager;
import client.utility.ValidatorClient;
import common.utility.ResponseBuilder;

import java.util.NoSuchElementException;

public class RemoveByIdCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private String argument;

    
    public RemoveByIdCommand(String argument) {
        super("remove_by_id","удалить элемент из коллекции по его id");
        this.argument=argument;
    }

    public RemoveByIdCommand() {
        super("remove_by_id id","удалить элемент из коллекции по его id");
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public RemoveByIdCommand(CollectionManager collectionManager) {
        super("remove_by_id id","удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    public ExitCodeCommand execute() {
        ExitCodeCommand valid  = validate();
        if (!valid.equals(ExitCodeCommand.OK)){
            return valid;
        }
        try {
            if (collectionManager.getCollection().size()==0){
                throw new WrongAmountOfElementsException("Коллекция пуста!");
            }
            Integer id = Integer.valueOf(argument);
            if (ValidatorClient.validateIdVehicle(id, collectionManager.getCollection()) == false) {
                throw new ValidateDataException("Введенное поле id не валидно!");
            }
            collectionManager.removeById(id);
            return ExitCodeCommand.OK;
        }
        catch (WrongAmountOfElementsException e){
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.OK;
        }
        catch (ValidateDataException e) {
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
        catch (IndexOutOfBoundsException e){
            ResponseBuilder.appendLn("Коллекция пуста!");
            return ExitCodeCommand.OK;
        }
        catch (NoSuchElementException e){
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.CTRL_D;
        }
    }
    public ExitCodeCommand validate(){
        try {
            if (argument.isEmpty()) {
                throw new NullPointerException("id не может быть null!");
            }
            if (argument.split("\\s+").length>1){
                throw new WrongAmountOfElementsException("Должен быть только один аргумент - поле 'id'!");
            }
            Integer id = Integer.valueOf(argument);
            if (ValidatorClient.validateIdVehicle2(id) == false) {
                throw new ValidateDataException("Введенное поле id не валидно!");
            }
            return ExitCodeCommand.OK;
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
        catch (WrongAmountOfElementsException e){
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
        catch (NumberFormatException e) {
            System.out.println(e.getMessage() + " : Некорректный ввод поля id!");
            ResponseBuilder.appendLn(e.getMessage() + " : Некорректный ввод поля id!");
            return ExitCodeCommand.ERROR;
        }
        catch (ValidateDataException e) {
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }
}