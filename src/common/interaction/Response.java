package common.interaction;

import common.ExitCodeCommand;

import java.io.Serializable;

public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ExitCodeCommand status;
    private final String message;
    private final String commandName;
    private  String fileName;
    private  byte[] fileData;

    public Response(ExitCodeCommand status, String message) {
        this(status, message, null, null, null);
    }

    public Response(ExitCodeCommand status, String message, String commandName) {
        this(status, message, commandName, null, null);
    }

    public Response(ExitCodeCommand status, String message, String fileName, byte[] fileData) {
        this(status, message, null, fileName, fileData);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public Response(ExitCodeCommand status, String message, String commandName, String fileName, byte[] fileData) {
        this.status = status;
        this.message = message;
        this.commandName = commandName;
        this.fileName = fileName;
        this.fileData = fileData;
    }



    public ExitCodeCommand getStatus() { return status; }
    public String getMessage() { return message; }
    public String getCommandName() { return commandName; }
    public String getFileName() { return fileName; }
    public byte[] getFileData() { return fileData; }

    public boolean isSuccess() {
        return status == ExitCodeCommand.OK || status == ExitCodeCommand.EXIT;
    }
}