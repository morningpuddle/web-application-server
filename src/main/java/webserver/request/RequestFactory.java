package webserver.request;

import db.DataBase;
import util.HttpRequestUtils;

import java.util.Map;

public class RequestFactory {
    private final DataBase userDatabase;

    public RequestFactory(DataBase db) {
        this.userDatabase = db;
    }

    public Request createNewUserRequest(String body) {
        Map<String, String> args = HttpRequestUtils.parseQueryString(body);
        return new NewUserRequest(userDatabase, args);
    }

    public Request createGetRequest(String url) {
        return new GetRequest(url);
    }
}
