package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import webserver.request.HealthCheckRequest;
import webserver.request.NewUserRequest;
import webserver.request.GetRequest;
import webserver.request.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static webserver.request.Request.WebRequestType.UNDEFINED;

public class RequestUtils {
    private static final String FILE_REGEX = ".*/(css|images|fonts|js|qna|user|index|favicon).*";
    private static final String USER_CREATE_REGEX = ".*/user/create?.*";

    public static Request createRequest(BufferedReader bufferedReader) throws IOException {
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

        switch (method) {
            case GET:
                return createGetRequest(url);
            case POST:
                return createNewUserRequest(bufferedReader, contentLength);
            case UNDEFINED:
                // TODO: 404 request
        }
        // TODO: 404 request
        return new HealthCheckRequest();
    }

    private static Request createNewUserRequest(BufferedReader bufferedReader, int contentLength) throws IOException {
        String body = IOUtils.readData(bufferedReader, contentLength);
        Map<String, String> args = HttpRequestUtils.parseQueryString(body);
        return new NewUserRequest(args);
    }

    private static Request createGetRequest(String url) {
        return new GetRequest(url);
    }

    private static boolean isFile(String url) {
        return url.matches(FILE_REGEX);
    }

    private static boolean isUserCreate(String url) {
        return url.matches(USER_CREATE_REGEX);
    }
}
