package common.interaction;

import java.io.Serializable;
import java.util.UUID;

public class ResponseChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID requestId;
    private final int chunkNumber;
    private final int totalChunks;
    private final byte[] data;           
    private final boolean isLast;

    public ResponseChunk(UUID requestId, int chunkNumber, int totalChunks, byte[] data, boolean isLast) {
        this.requestId = requestId;
        this.chunkNumber = chunkNumber;
        this.totalChunks = totalChunks;
        this.data = data;
        this.isLast = isLast;
    }

    
    public UUID getRequestId() { return requestId; }
    public int getChunkNumber() { return chunkNumber; }
    public int getTotalChunks() { return totalChunks; }
    public byte[] getData() { return data; }
    public boolean isLast() { return isLast; }
}