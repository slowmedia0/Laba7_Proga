package common.commands;

import common.ExitCodeCommand;
import common.utility.ResponseBuilder;
import server.database.UserDAO;
import server.utility.CollectionManager;

import java.util.Scanner;

public class RegisterCommand extends AbstractCommand {
    private String argument;
    private String login;
    private String password;

    public RegisterCommand(String argument, boolean isInteractive) {
        super("register", "Регистрация нового пользователя");
        this.argument = argument;

        if (isInteractive) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите login для регистрации: ");
            this.login = scanner.nextLine().trim();

            System.out.print("Введите password: ");
            this.password = scanner.nextLine().trim();
        }
    }

    public RegisterCommand(String argument) {
        super("register", "Регистрация нового пользователя");
        this.argument = argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    @Override
    public ExitCodeCommand execute() {
        ExitCodeCommand valid = validate();
        if (!valid.equals(ExitCodeCommand.OK)) {
            return valid;
        }

        if (login == null || password == null || login.trim().isEmpty() || password.trim().isEmpty()) {
            ResponseBuilder.append("Логин и пароль не могут быть пустыми");
            return ExitCodeCommand.ERROR;
        }

        boolean success = UserDAO.register(login, password);
        if (success) {
            ResponseBuilder.append(" Регистрация прошла успешно!");
            ResponseBuilder.append(" Вы автоматически вошли как " + login);
            CollectionManager.setCurrentUser(login);
            CollectionManager.setCurrentUserLogin(login);
            return ExitCodeCommand.OK;
        } else {
            ResponseBuilder.append(" Ошибка регистрации (возможно, такой логин уже занят)");
            return ExitCodeCommand.ERROR;
        }
    }

    @Override
    public ExitCodeCommand validate() {
        if (!argument.isEmpty()) {
            ResponseBuilder.append("Команда register не принимает аргументов!");
            return ExitCodeCommand.ERROR;
        }
        return ExitCodeCommand.OK;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}