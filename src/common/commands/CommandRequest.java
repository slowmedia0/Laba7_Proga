package common.commands;

import common.models.Vehicle;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private String nameOfCommand;
    private Object argument;
    private Vehicle vehicle;

    // === Поля, которые используются в CommandManger ===
    private String FileName;   // оставлено с большой буквы, как в CommandManger
    private byte[] FileData;

    // === НОВЫЕ ПОЛЯ ДЛЯ ЛР7 ===
    private String login;
    private String password;

    // ==================== Конструкторы ====================

    public CommandRequest(String nameOfCommand) {
        this.nameOfCommand = nameOfCommand;
    }

    public CommandRequest(String nameOfCommand, Object argument) {
        this.nameOfCommand = nameOfCommand;
        this.argument = argument;
    }

    public CommandRequest(String nameOfCommand, Vehicle vehicle) {
        this.nameOfCommand = nameOfCommand;
        this.vehicle = vehicle;
    }

    public CommandRequest(String nameOfCommand, Object argument, Vehicle vehicle) {
        this.nameOfCommand = nameOfCommand;
        this.argument = argument;
        this.vehicle = vehicle;
    }

    // ==================== Геттеры и Сеттеры ====================

    public String getNameOfCommand() {
        return nameOfCommand;
    }

    public Object getArgument() {
        return argument;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    // Геттеры для совместимости с CommandManger
    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        this.FileName = fileName;
    }

    public byte[] getFileData() {
        return FileData;
    }

    public void setFileData(byte[] fileData) {
        this.FileData = fileData;
    }

    // Методы, которые используются в RequestHandler
    public String getCommandName() {
        return nameOfCommand;
    }

    public Object getCommandArgument() {
        return argument;
    }

    public Vehicle getVehicleArgument() {
        return vehicle;
    }

    // Авторизация
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}