package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class Request {
    private static final Logger log = LoggerFactory.getLogger(Request.class);

    public enum WebRequestType {
        GET,
        POST,
        UNDEFINED;
    }

    protected WebRequestType type;

    public abstract void handleResponse(OutputStream out);

    public abstract String getArguments();

    protected static void responseHeader(DataOutputStream dos, String httpResponseCode, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + httpResponseCode + " OK \r\n");
            // TODO: Content-Type: ???
            dos.writeBytes("charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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
