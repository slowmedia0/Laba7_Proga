package server.utility;

import common.ExitCodeCommand;
import common.exceptions.CommandNotExist;
import common.models.Vehicle;
import common.utility.ResponseBuilder;

import java.util.ArrayList;

public class Console {

    public ExitCodeCommand exitCodeStatus = ExitCodeCommand.CTRL_C;

    private final CommandManger commandManager;
    private String loadFileName;           
    private byte[] loadFileData;           
    private boolean flagScript;
    private boolean flagReadCollection;

    private ArrayList<String> fields = new ArrayList<>(7);
    private CollectionManager collectionManager;

    public Console(CommandManger commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
    }

    public ExitCodeCommand getExitCodeStatus() { return exitCodeStatus; }
    public void setExitCodeStatus(ExitCodeCommand exitCodeStatus) { this.exitCodeStatus = exitCodeStatus; }
    public String getLoadFileName() { return loadFileName; }
    public byte[] getLoadFileData() { return loadFileData; }
    public ArrayList<String> getFields() { return fields; }
    public boolean isFlagScript() { return flagScript; }
    public boolean isFlagReadCollection() { return flagReadCollection; }

    

    
    public ExitCodeCommand launchCommand(String mnemonics, String argument) {
        try {
            if (isEmptyCommand(mnemonics, argument)) {
                exitCodeStatus = ExitCodeCommand.OK;
                return exitCodeStatus;
            }

            exitCodeStatus = commandManager.execute(mnemonics, argument);
            return exitCodeStatus;

        } catch (Exception e) {
            ResponseBuilder.appendLn("Ошибка выполнения команды: " + e.getMessage());
            exitCodeStatus = ExitCodeCommand.ERROR;
            return exitCodeStatus;
        }
    }

    
    public ExitCodeCommand launchCommand(String mnemonics, String argument, Vehicle vehicle, String FileName, byte[] FileData, String login, String password) {
        try {
            if (isEmptyCommand(mnemonics, argument)) {
                exitCodeStatus = ExitCodeCommand.OK;
                return exitCodeStatus;
            }

            exitCodeStatus = commandManager.execute(mnemonics, argument, vehicle, FileName, FileData, login, password);
            return exitCodeStatus;

        } catch (Exception e) {
            ResponseBuilder.appendLn("Ошибка выполнения команды: " + e.getMessage());
            exitCodeStatus = ExitCodeCommand.ERROR;
            return exitCodeStatus;
        }
    }

    private boolean isEmptyCommand(String mnemonics, String argument) {
        return (mnemonics == null || mnemonics.trim().isEmpty()) &&
                (argument == null || argument.trim().isEmpty());
    }


}