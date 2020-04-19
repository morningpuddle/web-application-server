package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

public class ListRequest extends Request {
    private static final Logger log = LoggerFactory.getLogger(GetRequest.class);

    private final boolean logged;
    private final String baseDir;

    public ListRequest(String baseDir, boolean logged) {
        this.logged = logged;
        this.baseDir = baseDir;
    }

    @Override
    public void handleResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        if (!logged) {
            responseHeader(dos, 302, "Found",
                    Map.of("Location", "/user/login.html", "Set-Cookie", "logined=false")
            );
        } else {
            try {
                byte[] bytes = Files.readAllBytes(new File(baseDir + "/user/list.html").toPath());
                responseHeader(dos, 200, "OK", bytes.length);
                responseBody(dos, bytes);
            } catch (IOException e) {
                log.error("Failed to handle response for GetRequest", e);
            }
        }
    }

    @Override
    public String getArguments() {
        return null;
    }
}
