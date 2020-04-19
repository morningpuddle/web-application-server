package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

public abstract class Request {
    private static final Logger log = LoggerFactory.getLogger(Request.class);

    public enum WebRequestType {
        GET,
        LOGIN,
        POST,
        UNDEFINED;
    }

    protected WebRequestType type;

    public abstract void handleResponse(OutputStream out);

    public abstract String getArguments();

    protected static void responseHeader(DataOutputStream dos, int responseCode, String responseMessage, Map<String, String> fields) {
        try {
            dos.writeBytes("HTTP/1.1 " + responseCode + " " + responseMessage + "\r\n");
            dos.writeBytes("charset=utf-8\r\n");
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    protected static void responseHeader(DataOutputStream dos, int httpResponseCode, String httpResponseMessage, int lengthOfBodyContent) {
        responseHeader(dos, httpResponseCode, httpResponseMessage,
                Collections.singletonMap("Content-Length", "" + lengthOfBodyContent));
    }

    protected static void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
