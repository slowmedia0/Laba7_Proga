package common.commands;

import common.ExitCodeCommand;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.ResponseBuilder;


import java.util.List;

public class HelpCommand extends AbstractCommand{
    private List<Command> commands;
    private String argument;

    
    public HelpCommand(String argument) {
        super("help","вывести справку по доступным командам");
        this.argument=argument;
    }

    public HelpCommand() {
        super("help","вывести справку по доступным командам");
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public ExitCodeCommand execute(){
        ExitCodeCommand valid  = validate();
        if (!valid.equals(ExitCodeCommand.OK)){
            return valid;
        }
        for (int i=0;i<commands.size();i++){
            ResponseBuilder.append(commands.get(i).getNameOfCommand() + " : " + commands.get(i).getDescriptionOfCommand());
        }
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