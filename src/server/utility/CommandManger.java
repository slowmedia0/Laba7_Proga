package server.utility;

import common.ExitCodeCommand;
import common.commands.*;
import common.exceptions.CommandNotExist;
import common.models.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManger {

    private final List<Command> commands;

    private final HelpCommand helpCommand;
    private final InfoCommand infoCommand;
    private final ShowCommand showCommand;
    private final AddCommand addCommand;
    private final UpdateIdCommand updateIdCommand;
    private final RemoveByIdCommand removeByIdCommand;
    private final ClearCommand clearCommand;
    private final ExecuteScriptCommand executeScriptCommand;
    private final ExitCommand exitCommand;
    private final RemoveGreaterCommand removeGreaterCommand;
    private final ReorderCommand reorderCommand;
    private final SortCommand sortCommand;
    private final SumOfEnginePowerCommand sumOfEnginePowerCommand;
    private final PrintFieldAscendingNumberOfWheelsCommand printFieldAscendingNumberOfWheelsCommand;
    private final PrintFieldDescendingNumberOfWheelsCommand printFieldDescendingNumberOfWheelsCommand;

    
    private final LoginCommand loginCommand;
    private final RegisterCommand registerCommand;
    private final LogoutCommand logoutCommand;

    public CommandManger(HelpCommand helpCommand, InfoCommand infoCommand, ShowCommand showCommand,
                         AddCommand addCommand, UpdateIdCommand updateIdCommand, RemoveByIdCommand removeByIdCommand,
                         ClearCommand clearCommand, ExecuteScriptCommand executeScriptCommand, ExitCommand exitCommand,
                         RemoveGreaterCommand removeGreaterCommand, ReorderCommand reorderCommand, SortCommand sortCommand,
                         SumOfEnginePowerCommand sumOfEnginePowerCommand,
                         PrintFieldAscendingNumberOfWheelsCommand printFieldAscendingNumberOfWheelsCommand,
                         PrintFieldDescendingNumberOfWheelsCommand printFieldDescendingNumberOfWheelsCommand,
                         LoginCommand loginCommand, RegisterCommand registerCommand, LogoutCommand logoutCommand) {

        this.helpCommand = helpCommand;
        this.infoCommand = infoCommand;
        this.showCommand = showCommand;
        this.addCommand = addCommand;
        this.updateIdCommand = updateIdCommand;
        this.removeByIdCommand = removeByIdCommand;
        this.clearCommand = clearCommand;
        this.executeScriptCommand = executeScriptCommand;
        this.exitCommand = exitCommand;
        this.removeGreaterCommand = removeGreaterCommand;
        this.reorderCommand = reorderCommand;
        this.sortCommand = sortCommand;
        this.sumOfEnginePowerCommand = sumOfEnginePowerCommand;
        this.printFieldAscendingNumberOfWheelsCommand = printFieldAscendingNumberOfWheelsCommand;
        this.printFieldDescendingNumberOfWheelsCommand = printFieldDescendingNumberOfWheelsCommand;

        this.loginCommand = loginCommand;
        this.registerCommand = registerCommand;
        this.logoutCommand = logoutCommand;

        this.commands = new ArrayList<>(Arrays.asList(helpCommand, infoCommand, showCommand, addCommand,
                updateIdCommand, removeByIdCommand, clearCommand, executeScriptCommand, exitCommand,
                removeGreaterCommand, reorderCommand, sortCommand, sumOfEnginePowerCommand,
                printFieldAscendingNumberOfWheelsCommand, printFieldDescendingNumberOfWheelsCommand,
                loginCommand, registerCommand, logoutCommand));
    }

    private boolean isAuthCommand(String mnemonics) {
        return "login".equalsIgnoreCase(mnemonics) ||
                "register".equalsIgnoreCase(mnemonics) ||
                "logout".equalsIgnoreCase(mnemonics);
    }

    
    public ExitCodeCommand login(String argument, String login, String password) {
        loginCommand.setArgument(argument);
        if (loginCommand instanceof LoginCommand) {
            ((LoginCommand) loginCommand).setLogin(login);
            ((LoginCommand) loginCommand).setPassword(password);
        }
        return loginCommand.execute();
    }

    public ExitCodeCommand register(String argument, String login, String password) {
        registerCommand.setArgument(argument);
        if (registerCommand instanceof RegisterCommand) {
            ((RegisterCommand) registerCommand).setLogin(login);
            ((RegisterCommand) registerCommand).setPassword(password);
        }
        return registerCommand.execute();
    }

    public ExitCodeCommand logout(String argument) {
        logoutCommand.setArgument(argument);
        return logoutCommand.execute();
    }

    
    public ExitCodeCommand execute(String mnemonics, String argument) throws CommandNotExist {
        if (isAuthCommand(mnemonics)) {
            if ("login".equalsIgnoreCase(mnemonics)) return login(argument, "", "");
            if ("register".equalsIgnoreCase(mnemonics)) return register(argument, "", "");
            if ("logout".equalsIgnoreCase(mnemonics)) return logout(argument);
        }

        String cmd = (mnemonics == null) ? "" : mnemonics.toLowerCase().trim();

        switch (cmd) {
            case "help": return help(argument);
            case "info": return info(argument);
            case "show": return show(argument);
            case "add": return add(argument);
            case "update": return updateById(argument);
            case "remove_by_id": return removeById(argument);
            case "clear": return clear(argument);
            case "execute_script": return executeScript(argument);
            case "exit": return exit(argument);
            case "remove_greater": return removeGreater(argument);
            case "reorder": return reorder(argument);
            case "sort": return sort(argument);
            case "sum_of_engine_power": return sumOfEnginePower(argument);
            case "print_field_ascending_number_of_wheels": return printFieldAscendingNumberOfWheels(argument);
            case "print_field_descending_number_of_wheels": return printFieldDescendingNumberOfWheels(argument);
            default:
                throw new CommandNotExist("Команда " + mnemonics + " не существует!");
        }
    }

    
    public ExitCodeCommand execute(String mnemonics, String argument, Vehicle vehicle, String FileName, byte[] FileData) throws CommandNotExist {
        if (isAuthCommand(mnemonics)) {
            if ("login".equalsIgnoreCase(mnemonics)) return login(argument, "", "");
            if ("register".equalsIgnoreCase(mnemonics)) return register(argument, "", "");
            if ("logout".equalsIgnoreCase(mnemonics)) return logout(argument);
        }

        String cmd = (mnemonics == null) ? "" : mnemonics.toLowerCase().trim();

        switch (cmd) {
            case "help": return help(argument, vehicle, FileName, FileData);
            case "info": return info(argument, vehicle, FileName, FileData);
            case "show": return show(argument, vehicle, FileName, FileData);
            case "add": return add(argument, vehicle, FileName, FileData);
            case "update": return updateById(argument, vehicle, FileName, FileData);
            case "remove_by_id": return removeById(argument, vehicle, FileName, FileData);
            case "clear": return clear(argument, vehicle, FileName, FileData);
            case "execute_script": return executeScript(argument, vehicle, FileName, FileData);
            case "exit": return exit(argument, vehicle, FileName, FileData);
            case "remove_greater": return removeGreater(argument, vehicle, FileName, FileData);
            case "reorder": return reorder(argument, vehicle, FileName, FileData);
            case "sort": return sort(argument, vehicle, FileName, FileData);
            case "sum_of_engine_power": return sumOfEnginePower(argument, vehicle, FileName, FileData);
            case "print_field_ascending_number_of_wheels": return printFieldAscendingNumberOfWheels(argument, vehicle, FileName, FileData);
            case "print_field_descending_number_of_wheels": return printFieldDescendingNumberOfWheels(argument, vehicle, FileName, FileData);
            default:
                throw new CommandNotExist("Команда " + mnemonics + " не существует!");
        }
    }

    
    public ExitCodeCommand execute(String mnemonics, String argument, Vehicle vehicle, String FileName, byte[] FileData, String login, String password) throws CommandNotExist {

        if ("login".equalsIgnoreCase(mnemonics)) {
            return login(argument, login, password);
        }
        if ("register".equalsIgnoreCase(mnemonics)) {
            return register(argument, login, password);
        }
        if ("logout".equalsIgnoreCase(mnemonics)) {
            return logout(argument);
        }

        
        return execute(mnemonics, argument, vehicle, FileName, FileData);
    }

    
    public ExitCodeCommand help(String argument){
        helpCommand.setCommands(commands);
        helpCommand.setArgument(argument);
        return helpCommand.execute();
    }

    public ExitCodeCommand info(String argument){
        infoCommand.setArgument(argument);
        return infoCommand.execute();
    }

    public ExitCodeCommand show(String argument){
        showCommand.setArgument(argument);
        return showCommand.execute();
    }

    public ExitCodeCommand add(String argument){
        addCommand.setArgument(argument);
        return addCommand.execute();
    }

    public ExitCodeCommand updateById(String argument){
        updateIdCommand.setArgument(argument);
        return updateIdCommand.execute();
    }

    public ExitCodeCommand removeById(String argument){
        removeByIdCommand.setArgument(argument);
        return removeByIdCommand.execute();
    }

    public ExitCodeCommand clear(String argument){
        clearCommand.setArgument(argument);
        return clearCommand.execute();
    }

    public ExitCodeCommand executeScript(String argument){
        executeScriptCommand.setArgument(argument);
        return executeScriptCommand.execute();
    }

    public ExitCodeCommand exit(String argument){
        exitCommand.setArgument(argument);
        return exitCommand.execute();
    }

    public ExitCodeCommand removeGreater(String argument){
        removeGreaterCommand.setArgument(argument);
        return removeGreaterCommand.execute();
    }

    public ExitCodeCommand reorder(String argument){
        reorderCommand.setArgument(argument);
        return reorderCommand.execute();
    }

    public ExitCodeCommand sort(String argument){
        sortCommand.setArgument(argument);
        return sortCommand.execute();
    }

    public ExitCodeCommand sumOfEnginePower(String argument){
        sumOfEnginePowerCommand.setArgument(argument);
        return sumOfEnginePowerCommand.execute();
    }

    public ExitCodeCommand printFieldAscendingNumberOfWheels(String argument){
        printFieldAscendingNumberOfWheelsCommand.setArgument(argument);
        return printFieldAscendingNumberOfWheelsCommand.execute();
    }

    public ExitCodeCommand printFieldDescendingNumberOfWheels(String argument){
        printFieldDescendingNumberOfWheelsCommand.setArgument(argument);
        return printFieldDescendingNumberOfWheelsCommand.execute();
    }

    
    public ExitCodeCommand help(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        helpCommand.setCommands(commands);
        helpCommand.setArgument(argument);
        return helpCommand.execute();
    }

    public ExitCodeCommand info(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        infoCommand.setArgument(argument);
        return infoCommand.execute();
    }

    public ExitCodeCommand show(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        showCommand.setArgument(argument);
        return showCommand.execute();
    }

    public ExitCodeCommand add(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        addCommand.setArgument(argument);
        addCommand.setVehicle(vehicle);
        return addCommand.execute();
    }

    public ExitCodeCommand updateById(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        updateIdCommand.setArgument(argument);
        updateIdCommand.setVehicle(vehicle);
        return updateIdCommand.execute();
    }

    public ExitCodeCommand removeById(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        removeByIdCommand.setArgument(argument);
        return removeByIdCommand.execute();
    }

    public ExitCodeCommand clear(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        clearCommand.setArgument(argument);
        return clearCommand.execute();
    }

    public ExitCodeCommand executeScript(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        executeScriptCommand.setArgument(argument);
        executeScriptCommand.setFileName(FileName);
        executeScriptCommand.setFileData(FileData);
        return executeScriptCommand.execute();
    }

    public ExitCodeCommand exit(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        exitCommand.setArgument(argument);
        return exitCommand.execute();
    }

    public ExitCodeCommand removeGreater(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        removeGreaterCommand.setArgument(argument);
        removeGreaterCommand.setVehicle(vehicle);
        return removeGreaterCommand.execute();
    }

    public ExitCodeCommand reorder(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        reorderCommand.setArgument(argument);
        return reorderCommand.execute();
    }

    public ExitCodeCommand sort(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        sortCommand.setArgument(argument);
        return sortCommand.execute();
    }

    public ExitCodeCommand sumOfEnginePower(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        sumOfEnginePowerCommand.setArgument(argument);
        return sumOfEnginePowerCommand.execute();
    }

    public ExitCodeCommand printFieldAscendingNumberOfWheels(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        printFieldAscendingNumberOfWheelsCommand.setArgument(argument);
        return printFieldAscendingNumberOfWheelsCommand.execute();
    }

    public ExitCodeCommand printFieldDescendingNumberOfWheels(String argument, Vehicle vehicle, String FileName, byte[] FileData){
        printFieldDescendingNumberOfWheelsCommand.setArgument(argument);
        return printFieldDescendingNumberOfWheelsCommand.execute();
    }

    public ExitCommand getExitCommand() {
        return exitCommand;
    }
}