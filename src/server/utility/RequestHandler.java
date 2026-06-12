package server.utility;

import common.commands.CommandRequest;
import common.interaction.Response;
import common.utility.GZIPUtils;
import common.utility.ResponseBuilder;
import common.utility.Serializer;
import common.ExitCodeCommand;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

public class RequestHandler {

    private static final int BUFFER_SIZE = 262144;

    public static void handleRequest(DatagramChannel channel, Selector selector, Console console) {
        SocketAddress clientAddress = null;
        try {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            clientAddress = channel.receive(buffer);

            if (clientAddress == null) return;

            buffer.flip();
            byte[] requestBytes = new byte[buffer.remaining()];
            buffer.get(requestBytes);

            CommandRequest request = tryDeserialize(requestBytes);
            if (request == null) {
                try {
                    byte[] decompressed = GZIPUtils.decompress(requestBytes);
                    request = tryDeserialize(decompressed);
                } catch (Exception ignored) {}
            }

            if (request == null) {
                System.out.println("Не удалось десериализовать запрос");
                return;
            }

            System.out.println("<- Принят запрос: " + request.getCommandName() + " от " + clientAddress);

            Response response = processCommand(request, console);
            ResponseSender.sendResponse(channel, clientAddress, response);

        } catch (Exception e) {
            e.printStackTrace();
            ResponseBuilder.appendLn("Критическая ошибка сервера: " + e.getMessage());
            Response errorResponse = new Response(ExitCodeCommand.ERROR, ResponseBuilder.getOutput());
            try {
                if (clientAddress != null) {
                    ResponseSender.sendResponse(channel, clientAddress, errorResponse);
                }
            } catch (Exception ignored) {}
        }
        ResponseBuilder.clear();
    }

    private static Response processCommand(CommandRequest request, Console console) {
        try {
            String commandName = request.getCommandName();
            String argument = request.getCommandArgument() != null ? request.getCommandArgument().toString() : "";

            ExitCodeCommand result;

            // Специальная обработка для login и register
            if ("login".equalsIgnoreCase(commandName)) {
                result = console.launchCommand(commandName, argument, null, null, null,
                        request.getLogin(), request.getPassword());
            }
            else if ("register".equalsIgnoreCase(commandName)) {
                result = console.launchCommand(commandName, argument, null, null, null,
                        request.getLogin(), request.getPassword());
            }
            else if (request.getVehicleArgument() != null) {
                result = console.launchCommand(commandName, argument, request.getVehicleArgument(), null, null,null,null);
            }
            else {
                result = console.launchCommand(commandName, argument);
            }

            String statusMessage = (result == ExitCodeCommand.OK || result == ExitCodeCommand.EXIT)
                    ? "Команда выполнена успешно."
                    : "Команда выполнена с ошибками.";

            ResponseBuilder.append(statusMessage);
            return new Response(result, ResponseBuilder.getOutput());

        } catch (Exception e) {
            ResponseBuilder.appendLn("Ошибка выполнения команды '" + request.getCommandName() + "': " + e.getMessage());
            return new Response(ExitCodeCommand.ERROR, ResponseBuilder.getOutput());
        }
    }

    private static CommandRequest tryDeserialize(byte[] data) {
        try {
            return (CommandRequest) Serializer.deserialize(data);
        } catch (Exception e) {
            return null;
        }
    }
}