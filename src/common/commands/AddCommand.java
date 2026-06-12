package common.commands;

import client.utility.FieldReaderClient;
import common.ExitCodeCommand;
import common.models.Vehicle;
import server.utility.CollectionManager;
import common.utility.ResponseBuilder;

public class AddCommand extends AbstractCommand {
    private CollectionManager collectionManager;
    private String argument;
    private Vehicle vehicle;

    
    public AddCommand(String argument, boolean flag) {
         super("add", "добавить новый элемент в коллекцию");
         this.argument = argument;
         if (flag) {
             try {
                 this.vehicle = FieldReaderClient.askVehicleObject();
             } catch (Exception e) {
                 System.out.println(e.getMessage());
             }
         }
    }



    public AddCommand(CollectionManager collectionManager) {
        super("add","добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public AddCommand() {
        super("add","добавить новый элемент в коллекцию");
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ExitCodeCommand execute() {
        ExitCodeCommand valid  = validate();
        if (!valid.equals(ExitCodeCommand.OK)){
            return valid;
        }
        try{
            collectionManager.addToCollection(vehicle);
            return ExitCodeCommand.OK;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }


    public ExitCodeCommand validate(){
        try{
            if (!argument.isEmpty()){
                throw new IllegalStateException("Преждевременный ввод элемента!");
            }
            return ExitCodeCommand.OK;
        } catch (IllegalStateException e){
            System.out.println(e.getMessage());
            ResponseBuilder.appendLn(e.getMessage());
            return ExitCodeCommand.ERROR;
        }
    }


}