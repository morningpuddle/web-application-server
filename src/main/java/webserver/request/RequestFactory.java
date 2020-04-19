package webserver.request;

import db.DataBase;
import util.HttpRequestUtils;

import java.util.Map;

public class RequestFactory {
    private final DataBase userDatabase;
    private static final String DEFAULT_GET_DIR = "./webapp";

    public RequestFactory(DataBase db) {
        this.userDatabase = db;
    }

    public Request createNewUserRequest(String body) {
        Map<String, String> args = HttpRequestUtils.parseQueryString(body);
        return new NewUserRequest(userDatabase, args);
    }

    public Request createNewLoginRequest(String body) {
        Map<String, String> args = HttpRequestUtils.parseQueryString(body);
        return new LoginRequest(userDatabase, args);
    }

    public Request createGetRequest(String url) {
        return new GetRequest(DEFAULT_GET_DIR, url);
    }

    public Request createListRequest(boolean isLogged) {
        return new ListRequest(DEFAULT_GET_DIR, isLogged);
    }
}
