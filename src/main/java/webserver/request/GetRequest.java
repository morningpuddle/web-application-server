package webserver.request;

import com.google.common.annotations.VisibleForTesting;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

import static webserver.request.Request.WebRequestType.GET;

@Value
public class GetRequest extends Request {
    private static final Logger log = LoggerFactory.getLogger(GetRequest.class);
    private static final String DEFAULT_GET_DIR = "./webapp";

    String baseDir;
    String requestUrl;
    boolean loggedIn;

    public GetRequest(String requestUrl, boolean loggedIn) {
        this(DEFAULT_GET_DIR, requestUrl, loggedIn);
    }

    @VisibleForTesting
    public GetRequest(String baseDir, String requestUrl, boolean loggedIn) {
        type = GET;
        this.baseDir = baseDir;
        this.requestUrl = requestUrl;
        this.loggedIn = loggedIn;
    }

    @Override
    public void handleResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        if (!loggedIn && requestUrl.equals("/user/list.html")) {
            responseHeader(dos, 302, "Found",
                    Map.of("Location", "/user/login.html", "Set-Cookie", "logined=false")
            );
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(new File(baseDir + requestUrl).toPath());
            responseHeader(dos, 200, "OK", bytes.length);
            responseBody(dos, bytes);
        } catch (IOException e) {
            log.error("Failed to handle response for GetRequest", e);
        }
    }

    @Override
    public String getArguments() {
        return requestUrl;
    }
}
