package server;

import server.utility.Console;
import server.utility.RequestHandler;
import server.database.DatabaseManager;
import server.utility.CollectionManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer {

    private static int PORT;
    private static final int BUFFER_SIZE = 262144;

    private final DatagramChannel channel;
    private final Selector selector;
    private final Console console;

    
    private static final ExecutorService processingPool = Executors.newFixedThreadPool(10);

    public UDPServer(Console console, int port) throws IOException {
        this.console = console;
        this.PORT = port;

        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);

        try {
            this.channel.bind(new InetSocketAddress("0.0.0.0", PORT));
            System.out.println("Сервер успешно запущен на порту " + PORT);
            System.out.println("Ожидаем подключений");
        } catch (IOException e) {
            if (e.getMessage() != null &&
                    (e.getMessage().contains("Address already in use") ||
                            e.getMessage().contains("Cannot assign requested address"))) {
                System.out.println("Порт " + PORT + " уже занят!");
                System.exit(0);
            } else {
                throw e;
            }
        }

        this.selector = Selector.open();
        this.channel.register(selector, SelectionKey.OP_READ);
    }

    public void start() {
        try {
            while (true) {
                selector.select();

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isReadable()) {
                        
                        new Thread(() -> {
                            try {
                                
                                processingPool.submit(() -> {
                                    RequestHandler.handleRequest(channel, selector, console);
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            System.out.println("Критическая ошибка сервера: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            processingPool.shutdown();
            if (selector != null) selector.close();
            if (channel != null) channel.close();
            System.out.println("Работа сервера остановлена");
        } catch (IOException e) {
            System.out.println("Ошибка остановки сервера: " + e.getMessage());
        }
    }
}