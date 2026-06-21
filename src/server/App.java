package server;

import common.commands.*;
import server.utility.*;
import server.database.DatabaseManager;

public class App {
    static Console console;

    public static void main(String[] args) {
        System.out.println("Использование: java -jar server.jar <port>");
        System.out.println("Пример: java -jar server.jar 2222");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (console != null && !console.isFlagReadCollection()) {
                console.launchCommand("exit", "");
            }
            System.out.println("Завершение работы сервера");
        }));


        if (args.length != 1) {
            System.out.println("Необходимо указать один аргумент!");
            System.exit(1);
        }

        Integer port = FieldReaderServer.readPort(args[0]);

        try {
            DatabaseManager.connect();

            if (!DatabaseManager.isConnected()) {
                System.out.println("Предупреждение: Работа без базы данных (режим отладки)");
            }

            CollectionManager collectionManager = new CollectionManager();


            ExitCommand exitCommand = new ExitCommand();
            exitCommand.setCollectionManager(collectionManager);

            CommandManger commandManger = new CommandManger(
                    new HelpCommand(),
                    new InfoCommand(collectionManager),
                    new ShowCommand(collectionManager),
                    new AddCommand(collectionManager),
                    new UpdateIdCommand(collectionManager),
                    new RemoveByIdCommand(collectionManager),
                    new ClearCommand(collectionManager),
                    new ExecuteScriptCommand(),
                    exitCommand,
                    new RemoveGreaterCommand(collectionManager),
                    new ReorderCommand(collectionManager),
                    new SortCommand(collectionManager),
                    new SumOfEnginePowerCommand(collectionManager),
                    new PrintFieldAscendingNumberOfWheelsCommand(collectionManager),
                    new PrintFieldDescendingNumberOfWheelsCommand(collectionManager),
                    new LoginCommand(""),
                    new RegisterCommand(""),
                    new LogoutCommand("")
            );

            console = new Console(commandManger, collectionManager);

            FieldReaderServer.setConsole(console);
            collectionManager.loadFromDatabase();

            UDPServer server = new UDPServer(console, port);
            server.start();

        } catch (Exception e) {
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}