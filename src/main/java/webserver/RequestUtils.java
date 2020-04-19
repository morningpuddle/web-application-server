package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;
import webserver.request.HealthCheckRequest;
import webserver.request.Request;
import webserver.request.RequestFactory;

import java.io.BufferedReader;
import java.io.IOException;

import static webserver.request.Request.WebRequestType.UNDEFINED;

public class RequestUtils {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final String FILE_REGEX = ".*/(css|images|fonts|js|qna|user|index|favicon).*";
    private static final String USER_CREATE_REGEX = ".*/user/create?.*";

    public static Request parseRequest(RequestFactory factory, BufferedReader bufferedReader) throws IOException {
        Request.WebRequestType method = UNDEFINED;
        String url = "/";
        int contentLength = 0;

        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            if (line.contains("GET") && isFile(line)) {
                // TODO: fail if url missing?
                String[] tokens = line.split(" ");
                method = Request.WebRequestType.GET;
                url = tokens[1];
            } else if (line.contains("POST") && isUserCreate(line)) {
                String[] tokens = line.split(" ");
                method = Request.WebRequestType.POST;
                url = tokens[1];
            } else if (line.contains("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        log.debug("Parsed line: {} {} {}", method, url, contentLength);

        switch (method) {
            case GET:
                return factory.createGetRequest(url);
            case POST:
                String body = IOUtils.readData(bufferedReader, contentLength);
                return factory.createNewUserRequest(body);
            case UNDEFINED:
                // TODO: 404 request
        }
        // TODO: 404 request
        return new HealthCheckRequest();
    }

    private static boolean isFile(String url) {
        return url.matches(FILE_REGEX);
    }

    private static boolean isUserCreate(String url) {
        return url.matches(USER_CREATE_REGEX);
    }
}
