package client.utility;

import client.UDPClient;
import common.ExitCodeCommand;
import common.commands.*;
import common.exceptions.CommandNotExist;
import common.exceptions.ScriptRecursionException;
import common.exceptions.ValidateDataException;
import common.interaction.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class UserHandler {
    public ExitCodeCommand ExitCodeCommandStatus = ExitCodeCommand.CTRL_C;
    private UDPClient udpClient;
    private Scanner userScanner;
    private ArrayList<String> arguments;
    private boolean flagScript;
    private boolean flagReadCollection;
    private ArrayList<String> fields = new ArrayList<>(7);
    private FileManagerClient fileManagerClient;
    private String currentLogin = null;
    private String currentPassword = null;
    private volatile boolean isExiting = false;

    public ExitCodeCommand getExitCodeCommandStatus() {
        return ExitCodeCommandStatus;
    }

    public void setExitCodeCommandStatus(ExitCodeCommand ExitCodeCommandStatus) {
        this.ExitCodeCommandStatus = ExitCodeCommandStatus;
    }

    // Добавь геттер
    public boolean isExiting() {
        return isExiting;
    }

    public UserHandler(UDPClient udpClient, Scanner userScanner, FileManagerClient fileManagerClient) {
        this.udpClient = udpClient;
        this.userScanner = userScanner;
        this.fileManagerClient = fileManagerClient;
        this.arguments=new ArrayList<>();
    }
    private Response sendAndCheck(CommandRequest request) {
        Response response = udpClient.sendRequest(request);

        if (response == null) {
            System.out.println("Сервер временно недоступен. Нет ответа.");
            return null;
        }
        if (!response.isSuccess()) {
            if (request.getNameOfCommand().equalsIgnoreCase("exit")) {
                System.out.println(response.getMessage());
                udpClient.close();
                System.exit(0);
            }
            return response;
        }
        return response;
    }

    public UDPClient getUdpClient() {
        return udpClient;
    }



    public ArrayList<String> getFields() {
        return fields;
    }

    public boolean isFlagReadCollection() {
        return flagReadCollection;
    }

    public boolean isFlagScript() {
        return flagScript;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public ExitCodeCommand scriptMode(String argument){
        ExitCodeCommand flagSuccessExecute = ExitCodeCommand.OK;
        try {
            int n=-1;
            boolean flagElemCommand = true;
            int index=n;
            arguments.add(argument);
            String mnemonics = "";
            String arg = "";
            if (fileManagerClient.readScript(argument)==null){
                throw new NullPointerException("");
            }
            for (var maybeCommand: fileManagerClient.readScript(argument)){
                try {
                    n += 1;
                    ArrayList<String> command = new ArrayList<>(2);
                    for (var i : maybeCommand.trim().split("\\s+", 2)) {
                        command.add(i);
                    }
                    if (command.size() ==1) {
                        command.add("");
                    }

                    if (flagElemCommand == true) {
                        if (command.get(0).equals("execute_script") && arguments.contains(command.get(1))) {
                            File file1 = new File(argument);
                            for (int i = 0; i < arguments.size(); i++) {
                                File file2 = new File(arguments.get(i));
                                if (file1.getAbsolutePath().equals(file2.getAbsolutePath())) {
                                    String errorMsg = "Не удалось выполнить без ошибок команду " + command.get(0) + " " + command.get(1)
                                            + " в скрипте " + argument + " ! Рекурсивный вызов скрипта '" + command.get(1) + "'!";
                                    System.out.println(errorMsg);
                                    throw new ScriptRecursionException(errorMsg);
                                }
                            }
                        } else if (command.get(0).equals("add") || command.get(0).equals("update") || command.get(0).equals("remove_greater")) {
                            flagElemCommand = false;
                            index = n + 7;
                            mnemonics = command.get(0);
                            arg = command.get(1);
                        } else if (executeCommandFromScript(command.get(0), command.get(1)).equals(ExitCodeCommand.OK) == false) {
                            if (command.get(0).equals("execute_script")) {
                                System.out.println("Не удалось выполнить без ошибок команду " + command.get(0) + " " + command.get(1) + " в скрипте " + argument + " !");
                            } else {
                                System.out.println("Не удалось выполнить команду " + command.get(0) + " " + command.get(1) + " в скрипте " + argument + " !");
                            }
                            flagSuccessExecute = ExitCodeCommand.ERROR;
                        } else {
                            System.out.println();
                        }
                    } else {
                        fields.add(maybeCommand);
                        if (n == index) {
                            ExitCodeCommand result = executeCommandWithVehicle(mnemonics, arg, new ArrayList<>(fields));

                            if (result != ExitCodeCommand.OK) {
                                System.out.println("Не удалось выполнить команду " + mnemonics + " " + arg + " в скрипте " + argument + " !");
                                flagSuccessExecute = ExitCodeCommand.ERROR;
                            } else {
                                System.out.println();
                            }

                            fields.clear();
                            flagElemCommand = true;
                        }
                    }
                }
                catch (ScriptRecursionException e){
                    flagSuccessExecute=ExitCodeCommand.ERROR;
                }
            }
        }
        catch (IllegalStateException | NullPointerException | IndexOutOfBoundsException e){
            System.out.println(e.getMessage() != null ? e.getMessage() : "Ошибка при выполнении скрипта");
            flagSuccessExecute= ExitCodeCommand.ERROR;
        }
        return flagSuccessExecute;
    }

    private boolean logout() {
        CommandRequest request = new CommandRequest("logout", "");
        request.setLogin(currentLogin);
        request.setPassword(currentPassword);

        Response response = sendAndCheck(request);

        if (response != null && response.isSuccess()) {
            this.currentLogin = null;
            this.currentPassword = null;
            System.out.println("✅ Вы вышли из аккаунта.");
            return true;
        }
        return false;
    }

    public boolean loginOrRegister() {
        System.out.println("\n=== АВТОРИЗАЦИЯ ===");

        while (true) {
            try {
                System.out.println("1. Войти (login)");
                System.out.println("2. Зарегистрироваться (register)");
                System.out.print("Выберите действие (1/2): ");
                String choice = userScanner.nextLine().trim();

                if (choice.equals("1")) {
                    if (login()) return true;
                } else if (choice.equals("2")) {
                    if (register()) return true;
                } else {
                    System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("\nВы использовали Ctrl+D.");
                ExitCodeCommandStatus = ExitCodeCommand.CTRL_D;
                handleExitResponse(null);
                return false;
            }
        }
    }

    private boolean login() {
        try {
            System.out.print("Введите login: ");
            String login = userScanner.nextLine().trim();
            System.out.print("Введите password: ");
            String password = userScanner.nextLine().trim();

            CommandRequest request = new CommandRequest("login", "");
            request.setLogin(login);
            request.setPassword(password);

            Response response = sendAndCheck(request);

            if (response == null) {
                System.out.println("❌ Не удалось связаться с сервером.");
                return false;
            }

            // Выводим сообщение от сервера
            if (response.getMessage() != null && !response.getMessage().trim().isEmpty()) {
                System.out.println(response.getMessage());
            }

            if (response.isSuccess()) {
                this.currentLogin = login;
                this.currentPassword = password;
                return true;
            } else {
                return false;
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nВы использовали Ctrl+D.");
            ExitCodeCommandStatus = ExitCodeCommand.CTRL_D;
            handleExitResponse(null);
            return false;
        } catch (Exception e) {
            System.out.println("❌ Не удалось связаться с сервером.");
            return false;
        }
    }

    private boolean register() {
        try {
            System.out.print("Введите login для регистрации: ");
            String login = userScanner.nextLine().trim();
            System.out.print("Введите password: ");
            String password = userScanner.nextLine().trim();

            CommandRequest request = new CommandRequest("register", "");
            request.setLogin(login);
            request.setPassword(password);

            Response response = sendAndCheck(request);

            if (response == null) {
                System.out.println("❌ Не удалось связаться с сервером.");
                return false;
            }

            // Выводим сообщение от сервера (единственный раз)
            if (response.getMessage() != null && !response.getMessage().trim().isEmpty()) {
                System.out.println(response.getMessage());
            }

            if (response.isSuccess()) {
                this.currentLogin = login;
                this.currentPassword = password;
                return true;
            } else {
                return false;
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nВы использовали Ctrl+D.");
            ExitCodeCommandStatus = ExitCodeCommand.CTRL_D;
            handleExitResponse(null);
            return false;
        } catch (Exception e) {
            System.out.println("❌ Не удалось связаться с сервером.");
            return false;
        }
    }
    private ExitCodeCommand executeCommandWithVehicle(String mnemonics, String argument, ArrayList<String> objectFields) {
        Command commandObject = createCommand(mnemonics, argument);
        if (commandObject == null) {
            return ExitCodeCommand.ERROR;
        }

        try {
            if (commandObject instanceof AddCommand) {
                ((AddCommand) commandObject).setVehicle(FieldReaderClient.askVehicleObject());
            } else if (commandObject instanceof UpdateIdCommand) {
                ((UpdateIdCommand) commandObject).setVehicle(FieldReaderClient.askVehicleObject());
            } else if (commandObject instanceof RemoveGreaterCommand) {
                ((RemoveGreaterCommand) commandObject).setVehicle(FieldReaderClient.askVehicleObject());
            }
        } catch (Exception e) {
            System.out.println("Ошибка создания объекта Vehicle: " + e.getMessage());
            return ExitCodeCommand.ERROR;
        }

        try {
            if (!commandObject.validate().equals(ExitCodeCommand.OK)) {
                throw new ValidateDataException("Команда '" + mnemonics + "' " + argument + " не валидна!");
            }
        }
        catch (ValidateDataException e){
            System.out.println(e.getMessage());
            return ExitCodeCommand.ERROR;
        }

        CommandRequest request = createCommandRequest(commandObject);
        Response response = sendAndCheck(request);   

        if (response != null && response.getMessage() != null && !response.getMessage().isEmpty()) {
            System.out.println("   " + response.getMessage());
        }

        return (response != null && response.isSuccess()) ? ExitCodeCommand.OK : ExitCodeCommand.ERROR;
    }

    public void interactiveMode() {
        flagReadCollection = false;

        // Первоначальная авторизация
        if (currentLogin == null) {
            boolean authSuccess = loginOrRegister();
            if (!authSuccess) {
                // Если Ctrl+D во время авторизации — сразу выходим
                return;
            }
        }

        try {
            while (true) {
                System.out.println("Введите команду");
                ArrayList<String> command = new ArrayList<>(2);

                if (!userScanner.hasNextLine()) {
                    throw new NoSuchElementException("Вы использовали Ctrl+D.");
                }

                String line = userScanner.nextLine().trim();
                ArrayList<String> parts = new ArrayList<>(Arrays.asList(line.split("\\s+", 2)));
                if (parts.size() == 1) {
                    parts.add("");
                }

                String mnemonics = parts.get(0);
                String argument = parts.get(1);

                if (mnemonics.isEmpty()) continue;

                Command commandObject = createCommand(mnemonics, argument);
                if (commandObject == null) continue;

                if ("logout".equalsIgnoreCase(mnemonics)) {
                    logout();
                    continue;
                }

                if (currentLogin == null && !isAuthCommand(mnemonics)) {
                    System.out.println("Ошибка: Вы не авторизованы. Используйте команды login или register.");
                    continue;
                }

                try {
                    if (!commandObject.validate().equals(ExitCodeCommand.OK)) {
                        throw new ValidateDataException("Команда не валидна!");
                    }
                } catch (ValidateDataException e) {
                    System.out.println(e.getMessage());
                    continue;
                }

                if ("execute_script".equalsIgnoreCase(mnemonics)) {
                    flagScript = true;
                    ExecuteScriptCommand esc = (ExecuteScriptCommand) commandObject;
                    esc.setUserHandler(this);
                    esc.execute();
                    continue;
                }

                CommandRequest request = createCommandRequest(commandObject);
                Response response = sendAndCheck(request);

                if ("exit".equalsIgnoreCase(mnemonics)) {
                    handleExitResponse(response);
                    return;
                }

                if (response != null && response.getMessage() != null && !response.getMessage().isEmpty()) {
                    System.out.println(response.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nВы использовали Ctrl+D.");
            ExitCodeCommandStatus = ExitCodeCommand.CTRL_D;
            handleExitResponse(null);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private ExitCodeCommand executeCommandFromScript(String mnemonics, String argument) {
        if ("login".equalsIgnoreCase(mnemonics) ||
                "register".equalsIgnoreCase(mnemonics) ||
                "logout".equalsIgnoreCase(mnemonics)) {

            System.out.println("→ Команда '" + mnemonics + "' пропущена в скрипте");
            return ExitCodeCommand.OK;
        }

        Command commandObject = createCommand(mnemonics, argument);
        if (commandObject == null) {
            return ExitCodeCommand.ERROR;
        }

        try {
            if (!commandObject.validate().equals(ExitCodeCommand.OK)) {
                throw new ValidateDataException("Команда '" + mnemonics + "' " + argument + " не валидна!");
            }
        } catch (ValidateDataException e) {
            System.out.println(e.getMessage());
            return ExitCodeCommand.ERROR;
        }

        if ("execute_script".equalsIgnoreCase(mnemonics)) {
            ExecuteScriptCommand esc = (ExecuteScriptCommand) commandObject;
            flagScript=true;
            esc.setUserHandler(this);
            String path = esc.getFileName() != null ? esc.getFileName() : argument;
            return scriptMode(path);
        }

        CommandRequest request = createCommandRequest(commandObject);
        Response response = sendAndCheck(request);   

        if ("exit".equalsIgnoreCase(mnemonics)) {
            handleExitResponse(response);
            return ExitCodeCommand.EXIT;
        }

        if (response != null && response.getMessage() != null && !response.getMessage().isEmpty()) {
            System.out.println("   " + response.getMessage());
        }

        return (response != null && response.isSuccess()) ? ExitCodeCommand.OK : ExitCodeCommand.ERROR;
    }


    public Command createCommand(String mnemonics, String argument) {
        try {
            switch (mnemonics) {
                case "help": return new HelpCommand(argument);
                case "info": return new InfoCommand(argument);
                case "show": return new ShowCommand(argument);
                case "add": return new AddCommand(argument, !flagScript);
                case "update": return new UpdateIdCommand(argument, !flagScript);
                case "remove_by_id": return new RemoveByIdCommand(argument);
                case "clear": return new ClearCommand(argument);
                case "exit": return new ExitCommand(argument);
                case "execute_script": return new ExecuteScriptCommand(argument);
                case "remove_greater": return new RemoveGreaterCommand(argument, !flagScript);
                case "reorder": return new ReorderCommand(argument);
                case "sort": return new SortCommand(argument);
                case "sum_of_engine_power": return new SumOfEnginePowerCommand(argument);
                case "print_field_ascending_number_of_wheels": return new PrintFieldAscendingNumberOfWheelsCommand(argument);
                case "print_field_descending_number_of_wheels": return new PrintFieldDescendingNumberOfWheelsCommand(argument);
                case "login":    return new LoginCommand(argument, true);
                case "register": return new RegisterCommand(argument, true);
                case "logout": return new LogoutCommand(argument);
                default:
                    if (!((mnemonics + argument).trim().isEmpty())) {
                        throw new CommandNotExist("Команда " + mnemonics + " не существует!");
                    }
                    return null;
            }
        } catch (CommandNotExist e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public CommandRequest createCommandRequest(Command command) {
        CommandRequest request;
        String cmdName = command.getNameOfCommand();

        if ("login".equals(cmdName)) {
            request = new CommandRequest("login", "");
            if (command instanceof LoginCommand) {
                LoginCommand cmd = (LoginCommand) command;
                request.setLogin(cmd.getLogin());
                request.setPassword(cmd.getPassword());
            }
            return request;
        }

        if ("register".equals(cmdName)) {
            request = new CommandRequest("register", "");
            if (command instanceof RegisterCommand) {
                RegisterCommand cmd = (RegisterCommand) command;
                request.setLogin(cmd.getLogin());
                request.setPassword(cmd.getPassword());
            }
            return request;
        }

        // Для остальных команд
        switch (cmdName) {
            case "help", "remove_by_id", "exit", "logout", "info", "show", "clear", "reorder", "sort",
                 "sum_of_engine_power", "print_field_ascending_number_of_wheels",
                 "print_field_descending_number_of_wheels":
                request = new CommandRequest(cmdName, command.getArgument());
                break;

            case "add":
                request = new CommandRequest(cmdName, command.getArgument(), ((AddCommand) command).getVehicle());
                break;

            case "update":
                request = new CommandRequest(cmdName, command.getArgument(), ((UpdateIdCommand) command).getVehicle());
                break;

            case "remove_greater":
                request = new CommandRequest(cmdName, command.getArgument(), ((RemoveGreaterCommand) command).getVehicle());
                break;

            default:
                request = new CommandRequest(cmdName, command.getArgument());
        }

        // Добавляем текущие credentials для обычных команд
        if (currentLogin != null && currentPassword != null &&
                !"login".equals(cmdName) && !"register".equals(cmdName)) {
            request.setLogin(currentLogin);
            request.setPassword(currentPassword);
        }

        return request;
    }

    public void handleExitResponse(Response response) {
        isExiting = true;
        if (response == null) {
            System.out.println("Сервер не вернул ответ при выходе.");
        } else {
            System.out.println(response.getMessage());
        }

        udpClient.close();
        ExitCodeCommandStatus = ExitCodeCommand.EXIT;
        System.out.println("Клиент завершает работу.");
    }
    private boolean isAuthCommand(String cmd) {
        return "login".equalsIgnoreCase(cmd) ||
                "register".equalsIgnoreCase(cmd) ||
                "logout".equalsIgnoreCase(cmd);
    }
}