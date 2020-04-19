package webserver.request;

import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static webserver.request.Request.WebRequestType.GET;

@Value
public class GetRequest extends Request {
    private static final Logger log = LoggerFactory.getLogger(GetRequest.class);
    private static final Pattern pattern = Pattern.compile(".*\\.(\\w+)");

    String baseDir;
    String requestUrl;

    public GetRequest(String baseDir, String requestUrl) {
        type = GET;
        this.baseDir = baseDir;
        this.requestUrl = requestUrl;
    }

    @Override
    public void handleResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        try {
            byte[] bytes = Files.readAllBytes(new File(baseDir + requestUrl).toPath());
            String fileExtension = getFileExtension(requestUrl);
            responseHeader(dos, 200, "OK",
                    Map.of("Content-Length", "" + bytes.length,
                           "Content-Type", fileExtension == null ? "text/html" : "text/" + fileExtension)
            );
            responseBody(dos, bytes);
        } catch (IOException e) {
            log.error("Failed to handle response for GetRequest", e);
        }
    }

    private static String getFileExtension(String requestUrl) {
        Matcher matcher = pattern.matcher(requestUrl);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public String getArguments() {
        return requestUrl;
    }
}
