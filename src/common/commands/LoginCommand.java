package common.commands;

import client.utility.UserHandler;
import common.ExitCodeCommand;
import common.utility.ResponseBuilder;
import server.database.UserDAO;
import server.utility.CollectionManager;

import java.util.Scanner;

public class LoginCommand extends AbstractCommand {
    private String argument;
    private String login;
    private String password;

    public LoginCommand(String argument, boolean isInteractive) {
        super("login", "Авторизация пользователя");
        this.argument = argument;

        if (isInteractive) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите login: ");
            this.login = scanner.nextLine().trim();

            System.out.print("Введите password: ");
            this.password = scanner.nextLine().trim();
        }
    }

    public LoginCommand(String argument) {
        super("login", "Авторизация пользователя");
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
        if (CollectionManager.getCurrentUserLogin() != null) {
            ResponseBuilder.append("Вы уже авторизованы как " + CollectionManager.getCurrentUserLogin() +
                    ". Используйте команду 'logout' для смены аккаунта.");
            return ExitCodeCommand.ERROR;
        }

        if (login == null || password == null || login.trim().isEmpty() || password.trim().isEmpty()) {
            ResponseBuilder.append("Логин и пароль не могут быть пустыми");
            return ExitCodeCommand.ERROR;
        }
        common.models.User user = UserDAO.login(login, password);
        if (user != null) {
            CollectionManager.setCurrentUser(login);
            CollectionManager.setCurrentUserLogin(login);
            ResponseBuilder.append(" Успешная авторизация как " + login);
            return ExitCodeCommand.OK;
        } else {
            ResponseBuilder.append(" Неверный логин или пароль");
            return ExitCodeCommand.ERROR;
        }
    }

    @Override
    public ExitCodeCommand validate() {
        if (!argument.isEmpty()) {
            ResponseBuilder.append("Команда login не принимает аргументов!");
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