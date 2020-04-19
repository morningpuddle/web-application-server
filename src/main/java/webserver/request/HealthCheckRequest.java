package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class HealthCheckRequest extends Request {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckRequest.class);

    @Override
    public void handleResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] bytes = "Hello World".getBytes();
        responseHeader(dos, 200, "OK", bytes.length);
        responseBody(dos, bytes);
    }

    @Override
    public String getArguments() {
        return null;
    }
}
