package server.utility;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import common.interaction.Response;
import common.interaction.ResponseChunk;
import common.utility.GZIPUtils;
import common.utility.Serializer;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResponseSender {

    //private static final Logger logger = LoggerFactory.getLogger(ResponseSender.class);

    private static final int MAX_UDP_SIZE = 65000;
    private static final int COMPRESS_THRESHOLD = 8192;
    private static final int MAX_CHUNK_SIZE = 8192;

    public static void sendResponse(DatagramChannel channel, SocketAddress clientAddress, Response response) {
        // === МИНИМАЛЬНОЕ ИЗМЕНЕНИЕ ПО ТЗ: многопоточная отправка ===
        new Thread(() -> {
            try {
                Thread.sleep(15);

                byte[] data = Serializer.serialize(response);

                if (data.length > COMPRESS_THRESHOLD) {
                    data = GZIPUtils.compress(data);
                    System.out.println("-> Ответ сжат GZIP (" + data.length + " байт | было " + Serializer.serialize(response).length + ")");
                    //  logger.info("-> Ответ сжат GZIP ({} байт)", data.length);
                }

                if (data.length <= MAX_UDP_SIZE) {
                    channel.send(ByteBuffer.wrap(data), clientAddress);
                    System.out.println("Отправлен ответ (" + data.length + " байт)");
                    // logger.info("Отправлен ответ ({} байт) клиенту {}", data.length, clientAddress);
                    return;
                }

                System.out.println("Ответ слишком большой (" + data.length + " байт). Разбиваем на чанки...");
                // logger.info("Ответ слишком большой ({} байт). Разбиваем на чанки...", data.length);

                UUID requestId = UUID.randomUUID();
                List<byte[]> chunks = splitIntoChunks(data, MAX_CHUNK_SIZE);

                for (int i = 0; i < chunks.size(); i++) {
                    boolean isLast = (i == chunks.size() - 1);
                    ResponseChunk chunk = new ResponseChunk(requestId, i, chunks.size(), chunks.get(i), isLast);

                    byte[] chunkData = Serializer.serialize(chunk);
                    channel.send(ByteBuffer.wrap(chunkData), clientAddress);

                    System.out.println("Отправлен чанк " + (i + 1) + "/" + chunks.size() + " (" + chunkData.length + " байт)");
                    //  logger.info("Отправлен чанк {}/{} ({} байт)", i + 1, chunks.size(), chunkData.length);

                    Thread.sleep(5);
                }

            } catch (Exception e) {
                System.out.println("Ошибка отправки ответа: " + e.getMessage());
                // logger.error("Ошибка отправки ответа клиенту {}", clientAddress, e);
                e.printStackTrace();
            }
        }).start();
    }

    private static List<byte[]> splitIntoChunks(byte[] data, int maxSize) {
        List<byte[]> chunks = new ArrayList<>();
        int offset = 0;
        while (offset < data.length) {
            int length = Math.min(maxSize, data.length - offset);
            chunks.add(java.util.Arrays.copyOfRange(data, offset, offset + length));
            offset += length;
        }
        return chunks;
    }
}