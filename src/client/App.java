package client;

import client.utility.FieldReaderClient;
import client.utility.FileManagerClient;
import client.utility.UserHandler;
import common.ExitCodeCommand;

import java.util.Scanner;


public class App {
    static UserHandler userHandler;
    static UDPClient udpClient;
    public static void main(String[] args) {
        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (userHandler != null && udpClient != null) {
                // ← Главное исправление:
                if (!userHandler.isExiting()) {
                    System.out.println("Вы использовали Ctrl+C.");
                    userHandler.handleExitResponse(
                            udpClient.sendRequest(
                                    userHandler.createCommandRequest(
                                            userHandler.createCommand("exit", "")
                                    )
                            )
                    );
                }
            }
        }));

        System.out.println("Использование: java -jar client.jar <host> <port>");
        System.out.println("Пример корректного использования: java -jar client.jar localhost 2222");

        
/*
        if (args.length != 2) {
            System.out.println("Необходимо указать два аргумента!");
           System.exit(1);
        }
        String host = FieldReaderClient.readHost(args[0]);
        Integer port = FieldReaderClient.readPort(args[1]);

 */
        String host = "localhost";
        int port = 2222;

        System.out.println("Клиент запускается");

        try (Scanner scanner = new Scanner(System.in)) {

            udpClient = new UDPClient(host, port);
            udpClient.connect();
            FileManagerClient fileManagerClient = new FileManagerClient();

            userHandler = new UserHandler(udpClient, scanner,fileManagerClient);

            FieldReaderClient.setUserHandler(userHandler);


            userHandler.interactiveMode();

        } catch (Exception e) {
            System.out.println("Критическая ошибка в клиенте:");
            e.printStackTrace();
        }

    }
}