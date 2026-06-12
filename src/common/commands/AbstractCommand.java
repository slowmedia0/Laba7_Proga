package common.commands;

import java.io.Serializable;


public abstract class AbstractCommand implements Command {
    String nameOfCommand;
    String descriptionOfCommand;
    StringBuilder output = new StringBuilder();
    public AbstractCommand(String nameOfCommand, String descriptionOfCommand) {
        this.descriptionOfCommand = descriptionOfCommand;
        this.nameOfCommand = nameOfCommand;
    }
    public String getNameOfCommand() {
        return nameOfCommand;
    }
    public String getDescriptionOfCommand() {
        return descriptionOfCommand;
    }

    public void println(String text) {
        output.append(text).append("\n");
    }

    public void print(String text) {
        output.append(text);
    }

    public String getOutput() {
        return output.toString().trim();
    }

    public void clearOutput() {
        output.setLength(0);
    }
}