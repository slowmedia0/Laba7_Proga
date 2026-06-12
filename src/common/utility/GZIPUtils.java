package common.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPUtils {

    public static byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length < 8192) {
            return data;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(data);
            gzos.finish();
            return baos.toByteArray();
        }
    }

    public static byte[] decompress(byte[] data) throws IOException {
        if (data == null || data.length == 0) return data;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(data))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }
}