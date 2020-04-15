package webserver;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestUtils {
    public static String getRequestedFile(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            if (line.contains("GET")) {
                String[] tokens = line.split(" ");
                return tokens[1];
            }
        }
        return null;
    }
}
