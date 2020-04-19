package webserver;

import db.DataBase;
import db.DataBaseImpl;
import model.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import webserver.request.GetRequest;
import webserver.request.NewUserRequest;
import webserver.request.Request;
import webserver.request.RequestFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class RequestUtilsTest {

    DataBase db;
    RequestFactory factory;

    private class TestDatabaseImpl implements DataBase {
        @Override
        public void addUser(User user) { }

        @Override
        public User findUserById(String userId) { return null; }

        @Override
        public Collection<User> findAll() { return null; }
    }

    @Before
    public void setup() {
        db = new TestDatabaseImpl();
        factory = new RequestFactory(db);
    }

    @Test
    public void createsGetRequest() throws IOException {
        String test = "GET /index.html HTTP/1.1";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        Request req = RequestUtils.parseRequest(factory, reader);

        assertThat(req, instanceOf(GetRequest.class));
    }

    @Test
    public void createsNewUserRequest() throws IOException {
        String test =
                "POST /user/create HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 48\n" +
                "\n" +
                "userId=abc&password=def&name=ghi&email=y%40a.com\n";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        Request req = RequestUtils.parseRequest(factory, reader);

        assertThat(req, instanceOf(NewUserRequest.class));
    }

    @Test
    @Ignore("TODO")
    public void creates404() throws IOException { }
}